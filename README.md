![doc](doc/img/bijection.svg)

# cryptic-sequences

**Note:** The library is close to being production ready, but it is not there yet. It is a matter of
days, though, before the first release is made. I want to do everything I can to make sure the algorithm
passes as many statistical tests as possible before someone starts using it in production and end-up with
stuck with a suboptimal algorithm.

## What

"cryptic-sequences" is a Kotlin multiplatform library that allows one to generate a pseudo-random numbers generator
(PRNG) that return numbers that are guaranteed to be unique. They are part of a growing family of so-called
*splittable* pseudo-random number generators (PRNG). Links to relevant papers are provided at the end of the document.

*cryptic-sequences* sacrifices speed for flexibility and simplicity. It can generate PRNGs that have periods
covering all the *words* that can be generated with *n* digits of a given base. Despite its simplicity,
the result of *cryptic-sequences* pass most statistical tests for randomness.

## Why

Being able to generate unique pseudo-random numbers is useful, for example, to generate unique identifiers
that are visible to the user, but that leak no obvious information about the number of identifiers that have
been generated so far, whether a given identifier has been generated before another one, etc.

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
--block-size [1024] -> Block size in values (not in bytes). When option "byte-count" is present and output is binary,
this option allows to speed-up output by writing multiple values at a time. { Int }
--output, -o -> File where to write the data. The standard input is used otherwise. { String }
--help, -h -> Usage info
```

Let's say you want to generate five unique numbers having eight decimal digits each. You do:

```
> java -jar cryptic-sequences-cli-shadow.jar --size 8 --count 5
46911695
50312533
59216998
04627193
90969238
```
To generate the next numbers, you have to provide the starting point. Let's start at three, to show the overlap.
```
> java -jar cryptic-sequences-cli-shadow.jar --size 8 --count 7 --start 3
04627193
90969238
70113075
24089412
02965207
95742707
92143982
```
To generate different sequences of number, you can use a different key. key "AAAAAA" (0 in base64) is the default,
by the way. Also, if you do not provide the full six digits of the key, it will be padded with 'A's to the right.
(the least significant digits are on the left).
```
> java -jar cryptic-sequences-cli-shadow.jar --size 8 --count 5 --key HeJWHF
95576382
84714087
22956595
12723002
37511511
```

To have a demonstration that all numbers are generated once and only once, one can
count the lines, and check that the number of lines stays the same when duplicates
are removed.
```
> java -jar cryptic-sequences-cli-shadow.jar --size 4 | wc -l
10000

> java -jar cryptic-sequences-cli-shadow.jar --size 4 | sort | uniq | wc -l
10000
```
## No, seriously, how ?

Generating pseudo-random unique numbers is one of those problems that appear extremely simple once you grasp the
required mathematical insights, but that is still kind of hard to do in practice.

### Encryption 101

The recipe is simple: use a sequence of numbers (0, 1, 2, 3, ...) and encrypt it in some way.
By design, an encryption algorithm (aka a cypher) generates values that appear to be random. Also, by definition
it is reversible; it is a [bijection](https://en.wikipedia.org/wiki/Bijection). The
generated numbers are thus guaranteed to be unique. Note that the reversibility requirement is what separates an
encryption algorithm from a hash.

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
- The table to store, or regenerate, can get impracticably large for large block sizes.

#### What can be done from there

One thing that can be done is to take a table of a reasonable size and apply it multiple times,
at different locations on the word, in order to quickly "shuffle" it to a new unpredictable value.
 
Let's say we take an array of the digits 0 to 99, and we shuffle it, getting those values:

<small>
[
 24, 54, 94, 80, 47, 40, 63, 97, 42, 96
 99, 44, 43, 22, 69, 79, 34, 41, 85, 37
 89, 93, 15, 17, 62, 76, 02, 45, 67, 08
 18, 58, 71, 29, 46, 78, 11, 07, 04, 27
 68, 09, 81, 95, 70, 75, 39, 72, 13, 19
 55, 83, 64, 28, 86, 06, 05, 31, 14, 20
 30, 00, 65, 82, 32, 56, 91, 98, 88, 35
 90, 92, 66, 49, 10, 48, 53, 74, 12, 23
 38, 21, 26, 52, 84, 03, 01, 57, 50, 77
 16, 51, 36, 73, 33, 59, 87, 25, 61, 60 
]

</small>

We could use them to fill a table directly, but to avoid getting cells in the table pointing to themselves, it
is better to interpret that sequence of number as one long cycle: 24 gives 54, 54 gives 94, etc. Using this cycle,
we can then fill a 10x10 table

<small>

|     |   0  |   1  |   2  |   3  |   4  |   5  |   6  |   7  |   8  |   9  |
|:---:|:----:|:-----|:----:|:----:|:----:|:----:|:----:|:----:|:----:|-----:|
|**0**|  65  |  57  |  45  |  01  |  27  |  31  |  05  |  04  |  18  |  81  |
|**1**|  48  |  07  |  23  |  19  |  20  |  17  |  51  |  62  |  58  |  55  |
|**2**|  30  |  26  |  69  |  38  |  54  |  61  |  52  |  68  |  86  |  46  |
|**3**|  00  |  14  |  56  |  59  |  41  |  90  |  73  |  89  |  21  |  72  |
|**4**|  63  |  85  |  96  |  22  |  43  |  67  |  78  |  40  |  53  |  10  |
|**5**|  77  |  36  |  84  |  74  |  94  |  83  |  91  |  50  |  71  |  87  |
|**6**|  24  |  60  |  76  |  97  |  28  |  82  |  49  |  08  |  09  |  79  |
|**7**|  75  |  29  |  13  |  33  |  12  |  39  |  02  |  16  |  11  |  34  |
|**8**|  47  |  95  |  32  |  64  |  03  |  37  |  06  |  25  |  35  |  93  |
|**9**|  92  |  98  |  66  |  15  |  80  |  70  |  99  |  42  |  88  |  44  |

</small>

Now, let's use that table to encrypt the value 0000
```
▼  ▼
0  0  0  0    t[0][0] = 65

   ▼  ▼
6  5  0  0    t[5][0] = 77

      ▼  ▼
6  7  7  0    t[7][0] = 75

6  7  7  5
```
A substitution sets one digit of the result, and the second digit influences
the next substitution. Substitution are compounded and that's what makes the
last digits of the word take values that appear so random. 

**Note on reversibility**

Since all substitutions are reversible operations, the operation as
a whole is also reversible. Of course, the table, as it is, makes it somewhat
hard. In practice, an inverse table is used to perform the decryption.

#### Refining the algorithm

The encryption above looks pretty good, but not so much if we pay closer
attention. Last digits are shuffled pretty well, but the first ones, not
so much. All words that start with *00* will always end-up being encrypted
to a word starting with *6*. That's not acceptable.

The way to fix this is to do multiple passes. A tried a lot of things
to improve on this approach, like applying passes in different directions,
rotating the word, etc., but nothing beats applying the algorithm
multiple times. Everything else only added complexity with no clear
benefits in terms of performance.

Now, how many passes should be done to have something that appears truly random ?
It depends on the size of the table used to shuffle the digits, and on the size
of the word. For small base systems, tables having additional dimensions are used,
so that the table size is of at least 100 elements. In practices, using hexadecimal
digits, 12 passes is enough to pass all the *dieharder* randomness tests.

**Note on the Dieharder test suite**

*Dieharder* is a program that allows one to evaluate the quality of a random number
generator using a suite of statistical tests. Those test include the famous
*Diehard* tests.

```
java -jar cryptic-sequences-cli-shadow.jar \
  --size 14 \
  --base 16 \
  --byte-count 7 \
  --strength 12 \
  | dieharder -g 200 -a
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  1.00e+06  |1892629328|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.40282710|  PASSED  
      diehard_operm5|   0|   1000000|     100|0.56845616|  PASSED  
  diehard_rank_32x32|   0|     40000|     100|0.40907378|  PASSED  
    diehard_rank_6x8|   0|    100000|     100|0.65956033|  PASSED  
   diehard_bitstream|   0|   2097152|     100|0.91516767|  PASSED  
        diehard_opso|   0|   2097152|     100|0.77904012|  PASSED  
        diehard_oqso|   0|   2097152|     100|0.87503398|  PASSED  
         diehard_dna|   0|   2097152|     100|0.25160202|  PASSED  
diehard_count_1s_str|   0|    256000|     100|0.17051264|  PASSED  
diehard_count_1s_byt|   0|    256000|     100|0.66111968|  PASSED  
 diehard_parking_lot|   0|     12000|     100|0.83744977|  PASSED  
    diehard_2dsphere|   2|      8000|     100|0.13039013|  PASSED  
    diehard_3dsphere|   3|      4000|     100|0.32879232|  PASSED  
     diehard_squeeze|   0|    100000|     100|0.25707882|  PASSED  
        diehard_sums|   0|       100|     100|0.14466473|  PASSED  
        diehard_runs|   0|    100000|     100|0.90400661|  PASSED  
        diehard_runs|   0|    100000|     100|0.19806263|  PASSED  
       diehard_craps|   0|    200000|     100|0.91344492|  PASSED  
       diehard_craps|   0|    200000|     100|0.42926218|  PASSED  
 marsaglia_tsang_gcd|   0|  10000000|     100|0.63758431|  PASSED  
 marsaglia_tsang_gcd|   0|  10000000|     100|0.82835757|  PASSED  
         sts_monobit|   1|    100000|     100|0.31181790|  PASSED  
            sts_runs|   2|    100000|     100|0.99653415|   WEAK   
          sts_serial|   1|    100000|     100|0.81499899|  PASSED  
          sts_serial|   2|    100000|     100|0.40059542|  PASSED  
          sts_serial|   3|    100000|     100|0.78464242|  PASSED  
          sts_serial|   3|    100000|     100|0.99244469|  PASSED  
          sts_serial|   4|    100000|     100|0.97396261|  PASSED  
          sts_serial|   4|    100000|     100|0.85079644|  PASSED  
          sts_serial|   5|    100000|     100|0.42226231|  PASSED  
          sts_serial|   5|    100000|     100|0.72713650|  PASSED  
          sts_serial|   6|    100000|     100|0.76398888|  PASSED  
          sts_serial|   6|    100000|     100|0.75633526|  PASSED  
          sts_serial|   7|    100000|     100|0.99956865|   WEAK   
          sts_serial|   7|    100000|     100|0.41033188|  PASSED  
          sts_serial|   8|    100000|     100|0.76711237|  PASSED  
          sts_serial|   8|    100000|     100|0.54783055|  PASSED  
          sts_serial|   9|    100000|     100|0.77729174|  PASSED  
          sts_serial|   9|    100000|     100|0.99411432|  PASSED  
          sts_serial|  10|    100000|     100|0.33515461|  PASSED  
          sts_serial|  10|    100000|     100|0.98348408|  PASSED  
          sts_serial|  11|    100000|     100|0.97260643|  PASSED  
          sts_serial|  11|    100000|     100|0.68675627|  PASSED  
          sts_serial|  12|    100000|     100|0.47067213|  PASSED  
          sts_serial|  12|    100000|     100|0.41807782|  PASSED  
          sts_serial|  13|    100000|     100|0.78440514|  PASSED  
          sts_serial|  13|    100000|     100|0.80033402|  PASSED  
          sts_serial|  14|    100000|     100|0.68297263|  PASSED  
          sts_serial|  14|    100000|     100|0.62709343|  PASSED  
          sts_serial|  15|    100000|     100|0.86053278|  PASSED  
          sts_serial|  15|    100000|     100|0.49204256|  PASSED  
          sts_serial|  16|    100000|     100|0.30389497|  PASSED  
          sts_serial|  16|    100000|     100|0.90982756|  PASSED  
         rgb_bitdist|   1|    100000|     100|0.55306620|  PASSED  
         rgb_bitdist|   2|    100000|     100|0.52865146|  PASSED  
         rgb_bitdist|   3|    100000|     100|0.12255423|  PASSED  
         rgb_bitdist|   4|    100000|     100|0.49673778|  PASSED  
         rgb_bitdist|   5|    100000|     100|0.19251975|  PASSED  
         rgb_bitdist|   6|    100000|     100|0.25783015|  PASSED  
         rgb_bitdist|   7|    100000|     100|0.51611580|  PASSED  
         rgb_bitdist|   8|    100000|     100|0.45255583|  PASSED  
         rgb_bitdist|   9|    100000|     100|0.95123390|  PASSED  
         rgb_bitdist|  10|    100000|     100|0.52790516|  PASSED  
         rgb_bitdist|  11|    100000|     100|0.63418730|  PASSED  
         rgb_bitdist|  12|    100000|     100|0.89886918|  PASSED  
rgb_minimum_distance|   2|     10000|    1000|0.38531021|  PASSED  
rgb_minimum_distance|   3|     10000|    1000|0.21840745|  PASSED  
rgb_minimum_distance|   4|     10000|    1000|0.50318433|  PASSED  
rgb_minimum_distance|   5|     10000|    1000|0.99876321|   WEAK   
    rgb_permutations|   2|    100000|     100|0.85250431|  PASSED  
    rgb_permutations|   3|    100000|     100|0.92195330|  PASSED  
    rgb_permutations|   4|    100000|     100|0.56457243|  PASSED  
    rgb_permutations|   5|    100000|     100|0.22225069|  PASSED  
      rgb_lagged_sum|   0|   1000000|     100|0.76375240|  PASSED  
      rgb_lagged_sum|   1|   1000000|     100|0.14184831|  PASSED  
      rgb_lagged_sum|   2|   1000000|     100|0.00965452|  PASSED  
      rgb_lagged_sum|   3|   1000000|     100|0.98527979|  PASSED  
      rgb_lagged_sum|   4|   1000000|     100|0.43745099|  PASSED  
      rgb_lagged_sum|   5|   1000000|     100|0.89793745|  PASSED  
      rgb_lagged_sum|   6|   1000000|     100|0.39595103|  PASSED  
      rgb_lagged_sum|   7|   1000000|     100|0.89462275|  PASSED  
      rgb_lagged_sum|   8|   1000000|     100|0.57025991|  PASSED  
      rgb_lagged_sum|   9|   1000000|     100|0.91383455|  PASSED  
```

Of course, it is not a good idea to use *cryptic-sequences* as a pseudo-random number
generator since it is around two orders of magnitude slower that a typical one.

## Other resources

### Other pseudo-random number generators that generate unique results

There are a number of other pseudo-random number generators out there that produce
unique numbers, sometimes as an undesired side effect. They are much faster algorithms
than what was exposed here. However, they do not offer the same flexibility.

#### SplittableRandom
SplittableRandom is a random number generator that has been introduced with Java 8. It has been
modified **not** to produce unique numbers, but in its original form, it did. It is an improvement
over an algorithm called *DotMix*. <br/>
[SplittableRandom paper](https://www.researchgate.net/publication/273188325_Fast_Splittable_Pseudorandom_Number_Generators)

#### SplitMix
SplitMix is a new random number generator for Haskell. It is an other algorithm derived from *DotMix*. <br/>
[SplitMix paper](https://www.researchgate.net/publication/273188325_Fast_Splittable_Pseudorandom_Number_Generators)
<br/>
[SplitMix blog post](https://www.tweag.io/blog/2020-06-29-prng-test/)

#### DotMix
The original algorithm that inspired many others. <br/>
[DotMix paper](https://www.researchgate.net/publication/221643681_Deterministic_Parallel_Random-Number_Generation_for_Dynamic-Multithreading_Platforms)

### Statistical tests

#### Dieharder
One of the most well known suite of statistical tests. Possibly a bit dated.
<br/>
[Dieharder project page](https://webhome.phy.duke.edu/~rgb/General/dieharder.php)

#### TestU01
From *l'Université de Montréal*. I studied some mathematics there, before going back to software
engineering. <br/>
[TestU01 project page](http://simul.iro.umontreal.ca/testu01/tu01.html)
<br/>
[TestU01 paper](https://www.researchgate.net/publication/278718594_TestU01)

Note: I have not tried it yet.

#### PracRand
"PractRand (Practically Random) is a C++ library of pseudo-random number
generators (PRNGs, or just RNGs) and statistical tests for RNGs." <br/>
[PracRand project page](https://sourceforge.net/projects/pracrand/)

Note: I have not tried it yet.
