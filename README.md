# cryptic-sequences

## What

"cryptic-sequences" is a library that allows one to generate pseudo-random numbers that are also guaranteed to be
unique.

## Why

This is useful, for example, to generate identifiers that are visible to the user, but that convey no information
about the number of identifiers that have been generated, whether a given identifier has been generated before an
other one, tc.

## How

Generating pseudo-random unique numbers is one of those problems that is extremely simple once you get the
mathematics insight, but that is still kind of hard to do in practice.

### Encryption 101

The recipe is simple: use a sequence of numbers (0, 1, 2, 3, ...) and encrypt it with some
algorithm. By design, an encryption algorithm generates values that appear to be random. Also, by definition
it is also reversible (it is a [bijection](https://en.wikipedia.org/wiki/Bijection)). The
generated numbers are thus guaranteed to be unique. If it was not the case, multiple source texts could be mapped to
the same encrypted text, making the operation irreversible. The reversibility is what separates an encryption
algorithm from a hash.

### Practical considerations

In practice, one would want to use a
[symmetric key algorithm](https://en.wikipedia.org/wiki/Symmetric-key_algorithm),
both for simplicity, performance, and because a
[public key algorithm](https://en.wikipedia.org/wiki/Public-key_cryptography) has no benefit for our use case.

Also, it is important to select a cypher that has a block size equal to the size of the identifiers we want to
generate. A stream cypher like RC4 can also be used for identifiers that have a size that is an exact number of
bytes (so not for *weird* number bases like 10).
**This is the problem** that this library solves, the inability to have a block size of an arbitrary length. However,
first, lets look at an example that uses a well known algorithm.

### Let's do it using DES

DES is an outdated encryption algorithm that is perfectly fine for this example. It has a relatively small block
size of *only* 64 bits. So we'll generate 64 bits identifiers.

```bash
> echo -n 0 | openssl des-ecb -e -K 1122334455667788 | xxd -p
67053908c77c76dc

> echo -n 1 | openssl des-ecb -e -K 1122334455667788 | xxd -p
56f557c71903753f

> echo -n 2 | openssl des-ecb -e -K 1122334455667788 | xxd -p
4fdb1007fc4abc77
```
You get the idea. What if you want a shorter identifier ? Well, you are out of luck with DES; there is no way
you can shorten the output and keep the uniqueness property. You have to find another algorithm. What about
RC4 ?

```bash
> echo -n 0 | openssl rc4-40 -e -K 1122334455 | xxd -p
da

> echo -n 1 | openssl rc4-40 -e -K 1122334455 | xxd -p
db

> echo -n 2 | openssl rc4-40 -e -K 1122334455 | xxd -p
d8

> echo -n 3 | openssl rc4-40 -e -K 1122334455 | xxd -p
d9
```
Ewww ! maybe not RC4. To be fair, I did not do any research on how to circumvent its weaknesses. Anyway, you get the
idea: there is room for improvement.

### Let's cook a new encryption algorithm

Well, we need to come up with a bijection that maps a set of numbers to elements of the same set, in the most
unpredictable way. Why not shuffle an array using a well known shuffling algorithm such as the
[Fisher–Yates shuffle](https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle) and some pseudo-random generator ?
There are many advantages to this technique:
- A cipher can be generated for a set of any size.
- It allows to leverage an existing pseudo-random generator; the hard work is already done.
- The key is simply the seed provided to the pseudo-random number generator.
- It is very fast once the table is generated.

There is obviously one major drawback:
- The table to store or regenerate can get impracticably large for large block sizes

#### What can be done from there

One thing that can be done is to take a table of a reasonable size and apply it multiple times, at different locations
on the number of the sequence, in order to quickly "shuffle" it to a new unpredictable value.
