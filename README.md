![doc](doc/img/bijection.svg)

# cryptic-sequences

## What

"cryptic-sequences" is a library that allows one to generate pseudo-random numbers that are also guaranteed to be
unique.

## Why

This is useful, for example, to generate unique identifiers that are visible to the user, but that leak no information
about the number of identifiers that have been generated so far, whether a given identifier has been generated before
another one, etc.

## How

### Usage

There is a command-line interface that can be used.

```
> java -jar cryptic-sequences-2.2.4-all.jar --help
Usage: cryptic-sequences-cli options_list
Options:
--base [10] -> The base to use for generated values. { Int }
--size [3] -> The number of digits each generated value should have. { Int }
--key [] -> The 48 bits key to use to encrypt the sequence, in base 64 (up to 6 digits). { String }
--start [0] -> The index from which to start in the sequence. { Int }
--strength [10] -> Controls the number of time the encryption algorithm is applied. { Int }
--count -> The number of generated values. { Int }
--byte-count -> Output values in binary mode, using "x" bytes for each number, truncating them if necessary. { Int }
--block-size [1024] -> Block size in values (not in bytes). When option "byte-count" is present and output is binary, this optionsallows to speed-up output by writing multiple values at a time. { Int }
--output, -o -> File where to write the data. The standard input is used otherwise. { String }
--help, -h -> Usage info
```

Let's say you want to generate five unique numbers having eight decimal digits each. You do:

```
> java -jar cryptic-sequences-2.2.4-all.jar --size 8 --count 5
47384794
68050747
89257120
91237406
16435778
```
To generate the next numbers, you have to provide the starting point. Let's start at three, to show the overlap.
```
> java -jar cryptic-sequences-2.2.4-all.jar --size 8 --count 7 --start 3
91237406
16435778
63565425
78608848
05488599
59062642
58836296
```
To generate different sequences of number, you can use a different key. key "AAAAAA" (0 in base64) is the default,
by the way. Also, if you do not provide the full six digits of the key, it will be padded with 'A's to the right.
(the least significant digit are on the left).
```
> java -jar cryptic-sequences-2.2.4-all.jar --size 8 --count 5 --key HeJWHF
09675205
45599541
96743281
83514778
75954843
```

## No, seriously, how ?

Generating pseudo-random unique numbers is one of those problems that appear extremely simple once you grasp the
required mathematical insights, but that is still kind of hard to do in practice.

### Encryption 101

The recipe is simple: use a sequence of numbers (0, 1, 2, 3, ...) and encrypt it in some way.
By design, an encryption algorithm (aka a cypher) generates values that appear to be random. Also, by definition
it is reversible, it is a [bijection](https://en.wikipedia.org/wiki/Bijection). The
generated numbers are thus guaranteed to be unique. Note that the reversibility requirement is what separates an
encryption algorithm from a hash.

#### Proof
If the generated numbers where not unique, it would mean that multiple source texts could be mapped to
the same encrypted text, making the operation irreversible, and thus not an encryption.


### Practical considerations

In practice, one would want to use a
[symmetric key algorithm](https://en.wikipedia.org/wiki/Symmetric-key_algorithm),
both for simplicity, performance, and because a
[public key algorithm](https://en.wikipedia.org/wiki/Public-key_cryptography) has no benefit for our use case.

Also, it is important to select a block cypher that has a block size equal to the size of the identifiers we want to
generate. A stream cypher like RC4 can also be used for identifiers that have a size that is an exact number of
bytes (so not for *weird* number bases like 10).
This is **the problem** that *cryptic-sequences* solves, the inability to have a block size of an arbitrary length. However,
first, lets look at examples that use well known cyphers.

### Let's do it using DES

The problem with well established, modern encryption algorithm, is that their block size is quite large. So, that's
why we are looking at DES, an outdated encryption algorithm that is perfectly fine for this example. It has a
relatively small block size of *only* 64 bits. So we'll generate 64 bits identifiers.

```
> echo -n 0 | openssl des-ecb -e -K 1122334455667788 | xxd -p
67053908c77c76dc

> echo -n 1 | openssl des-ecb -e -K 1122334455667788 | xxd -p
56f557c71903753f

> echo -n 2 | openssl des-ecb -e -K 1122334455667788 | xxd -p
4fdb1007fc4abc77
```
You get the idea. What if you want a shorter identifier ? Well, you are out of luck with DES because if one shortens
the output in any way, one looses the reversibility and thus the uniqueness of the generated numbers. Another
cypher with a smaller block size has to be used. What about RC4, being a stream cypher, it has a "block size" of
only one byte ?

```
> echo -n 0 | openssl rc4-40 -e -K 1122334455 | xxd -p
da

> echo -n 1 | openssl rc4-40 -e -K 1122334455 | xxd -p
db

> echo -n 2 | openssl rc4-40 -e -K 1122334455 | xxd -p
d8

> echo -n 3 | openssl rc4-40 -e -K 1122334455 | xxd -p
d9
```
Ewww ! maybe not RC4. To be fair, I didn't do any research on how to circumvent its weaknesses. Anyway, you get the
idea: there is room for improvement.

### Let's cook a new encryption algorithm

Well, we need to come up with a bijection that maps a set of numbers to elements of the same set, in the most
unpredictable way. Why not shuffle an array using a well known shuffling algorithm such as the
[Fisher–Yates shuffle](https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle) and some pseudo-random generator ?
There are many advantages to this technique:
- A cipher can be generated for a set of any size.
- It allows to leverage an existing pseudo-random generator; the hard work is already done.
- The key to our cypher is the seed provided to the pseudo-random number generator.
- It is very fast once the table is generated.

There is obviously one major drawback:
- The table to store or regenerate can get impracticably large for large block sizes.

#### What can be done from there

One thing that can be done is to take a table of a reasonable size and apply it multiple times, at different locations
on the number of the sequence, in order to quickly "shuffle" it to a new unpredictable value.

...
