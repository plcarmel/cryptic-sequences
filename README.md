![doc](doc/img/bijection.svg)

# cryptic-sequences

## What

*cryptic-sequences* is a Kotlin multiplatform library that allows one to generate a pseudo-random number generator
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

There is a command-line interface that can be used. It is available for both Linux native and JVM
platforms. On Linux, it uses the TestU01 statistical test suite. It should be installed prior to
executing the program. Otherwise, one can use the JVM version which is much, much faster anyway
(except for time it takes for the JVM to startup, of course).

```
> ./cryptic-sequences-cli.kexe --help
Usage: cryptic-sequences-cli options_list
Options: 
    --base [10]
        The base to use for generated values. { Int }
    --size [3]
        The number of digits each generated value should have. { Int }
    --key []
        The (up to) 64 bits key to use to encrypt the sequence, in base 16. { String }
    --start [0]
        The index from which to start in the sequence. { Int }
    --strength []
        Controls the number of time the encryption algorithm is applied. Default
        to 1.5 * size { Int }
    --count
        The number of generated values. { Int }
    --output, -o
        File where to write the data. The standard output is used otherwise. { String }
    --binary [false]
        Output random bits in binary mode, for the Dieharder test suite, for example. 
    --block-size [1024]
        Block size for binary output (number of 32 bits values). This option allows to
        speed-up output by writing multiple values at a time. { Int }
    --nbThreads, -j
        The number of thread to use. { Int }
    --test
        Name of the statistical tests to run
        { Value should be one of [small_crush, crush, big_crush, pseudo_diehard] }
    --time
        Measure the performance of the cypher. Takes the number of values to generate in
        parameter. { Int }
    --help, -h
        Usage info 
```

Let's say you want to generate five unique numbers having eight decimal digits each. You do:

```
> java -jar cryptic-sequences-cli-shadow.jar --size 8 --count 5
26415406
59462984
97028218
54675741
54440760
```
To generate the next numbers, you have to provide the starting point. Let's start at three, to
show the overlap.
```
> java -jar cryptic-sequences-cli-shadow.jar --size 8 --count 7 --start 3
54675741
54440760
03085018
44842718
69318970
38551579
90291526
```
To generate different sequences of numbers, you can use a different key. The key 0
is the default, by the way.
```
> java -jar cryptic-sequences-cli-shadow.jar --size 8 --count 5 --key 1
38262694
08096508
77804304
48070771
91259573
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
### Programming interface

Documentation to come.

## No, seriously, how ?

Here, the way *cryptic-sequences* work is described.

Generating pseudo-random numbers, that are also unique, is one of those problems that appear
extremely simple once you grasp the required mathematical insights, but that is still kind of
hard to do in practice.

### Encryption 101

The recipe is simple though: use a sequence of numbers (0, 1, 2, 3, ...) and encrypt it in some way.
By design, an encryption algorithm (aka a cypher) generates values that appear to be random. Also, by
definition it is reversible; it is a [bijection](https://en.wikipedia.org/wiki/Bijection). The
generated numbers are thus guaranteed to be unique. Note that the reversibility requirement is what
separates an encryption algorithm from a hash.

### Practical considerations

In practice, one would want to use a
[symmetric key algorithm](https://en.wikipedia.org/wiki/Symmetric-key_algorithm),
both for simplicity, performance, and because a
[public key algorithm](https://en.wikipedia.org/wiki/Public-key_cryptography) has no benefit for our use case.

Also, it is important to select a block cypher that has a block size equal to the size of the identifiers we
want to generate. A stream cypher like RC4 can also be used for identifiers that have a size that is an exact
number of bytes (so not for *weird* number bases like 10).

This is **the problem** that *cryptic-sequences* solves, the inability to have a block size of an arbitrary length. However,
first, let's look at examples that use well-known cyphers.

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
the output in any way, one loses the reversibility and thus the uniqueness of the generated numbers. Another
cypher with a smaller block size has to be used. What about RC4 ? Being a stream cypher, it has a "block size" of
only one byte.

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

### Let's Cook a New Encryption Algorithm

Well, we need to come up with a bijection that maps a set of numbers to elements of the same set, in the most
unpredictable way. Why not shuffle an array using a well-known shuffling algorithm such as the
[Fisher–Yates shuffle](https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle) and some pseudo-random generator ?
There are many advantages to this technique:
- A cipher can be generated for a set of any size.
- It allows to leverage an existing pseudo-random generator; the hard work is already done.
- The key to our cypher is the seed provided to the pseudo-random number generator.
- It is very fast once the table is generated.

There is obviously one major drawback:
- The table to store, or regenerate, can get impracticably large for large block sizes.

#### What Can Be Done From Here ?

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
is better to interpret that sequence of numbers as one long cycle: 24 gives 54, 54 gives 94, etc. Using this cycle,
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
the next substitution. Substitutions are compounded and that's what makes the
last digits of the word take values that appear so random. 

**Note on Reversibility**

Since all substitutions are reversible operations, the operation as
a whole is also reversible. Of course, the table, as it is, makes it somewhat
hard. In practice, an inverse table is used to perform the decryption.

#### Refining the algorithm ?

The encryption above looks pretty good, but not so much if we pay closer
attention. Last digits are shuffled pretty well, but the first ones, not
so much. All words that start with *00* will always end-up being encrypted
to a word starting with *6*. That's not acceptable.

One simple way to fix this is to do multiple passes. A lot of things have been
tried by myself to improve on my approach, like applying passes in different directions,
rotating the word, etc., but nothing beats applying the algorithm multiple times. Everything else
only added complexity with no clear benefits in terms of performance.

Note that for small base systems, tables having additional dimensions are used,
so that the table size is of at least 100 elements.

Now, how many passes should be done to have something that appears truly random ?
It seems to depend on the number of digits of the words. One and a half time the size
of the word seems to be enough to pass almost any statistical test you throw at it.

**Note on the Statistical Test Suite**

Two suites of tests have been used to aid in the development of the algorithm:
*Dieharder* and *TestU01* (the small_crush, crush and pseudo_diehard test batteries).

Statistical tests allow one to evaluate the quality of a random number
generator. Dieharder is an old one that has the merit of having a command line
interface and to present the results in a very concise way. The tests of the suite
include the famous *Diehard* tests.

Usually, a *p-value* is provided. It reports how well the result of the test applied to
the pseudo-random values fit the theoretical model. It represents how likely the
hypothesis that the numbers are truly random, or said otherwise, how likely is it that
the difference between the observed result and what the theoretical model predicts can be
explained as a simple statistical fluke.

A *p-value* of 0.5 means that the data fits perfectly. The closer one gets to 0 or to 1
though, the harder it is to put the result solely on bad luck.

Here is an example where the algorithm is used with a strength that is a bit low, with some
results being weak:

```
java -jar cryptic-sequences-cli/build/libs/cryptic-sequences-cli-2.4.2-SNAPSHOT-shadow.jar \
  --base 16 \
  --size 14 \
  --strength 12 \
  -j 8 \
  --binary \
  | dieharder -g 200 -a
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.52e+06  | 657964481|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.97707926|  PASSED  
      diehard_operm5|   0|   1000000|     100|0.14254846|  PASSED  
  diehard_rank_32x32|   0|     40000|     100|0.51549198|  PASSED  
    diehard_rank_6x8|   0|    100000|     100|0.11749555|  PASSED  
   diehard_bitstream|   0|   2097152|     100|0.94319861|  PASSED  
        diehard_opso|   0|   2097152|     100|0.09664094|  PASSED  
        diehard_oqso|   0|   2097152|     100|0.77460650|  PASSED  
         diehard_dna|   0|   2097152|     100|0.48486839|  PASSED  
diehard_count_1s_str|   0|    256000|     100|0.99975701|   WEAK   
diehard_count_1s_byt|   0|    256000|     100|0.87006945|  PASSED  
 diehard_parking_lot|   0|     12000|     100|0.86287213|  PASSED  
    diehard_2dsphere|   2|      8000|     100|0.04491306|  PASSED  
    diehard_3dsphere|   3|      4000|     100|0.24072608|  PASSED  
     diehard_squeeze|   0|    100000|     100|0.79959591|  PASSED  
        diehard_sums|   0|       100|     100|0.08979806|  PASSED  
        diehard_runs|   0|    100000|     100|0.76588651|  PASSED  
        diehard_runs|   0|    100000|     100|0.40093377|  PASSED  
       diehard_craps|   0|    200000|     100|0.07307879|  PASSED  
       diehard_craps|   0|    200000|     100|0.85564195|  PASSED  
 marsaglia_tsang_gcd|   0|  10000000|     100|0.18623214|  PASSED  
 marsaglia_tsang_gcd|   0|  10000000|     100|0.14489108|  PASSED  
         sts_monobit|   1|    100000|     100|0.17009823|  PASSED  
            sts_runs|   2|    100000|     100|0.48383154|  PASSED  
          sts_serial|   1|    100000|     100|0.41682053|  PASSED  
          sts_serial|   2|    100000|     100|0.38912901|  PASSED  
          sts_serial|   3|    100000|     100|0.22587563|  PASSED  
          sts_serial|   3|    100000|     100|0.93101426|  PASSED  
          sts_serial|   4|    100000|     100|0.69553816|  PASSED  
          sts_serial|   4|    100000|     100|0.98723772|  PASSED  
          sts_serial|   5|    100000|     100|0.92248526|  PASSED  
          sts_serial|   5|    100000|     100|0.38579192|  PASSED  
          sts_serial|   6|    100000|     100|0.96303567|  PASSED  
          sts_serial|   6|    100000|     100|0.27991197|  PASSED  
          sts_serial|   7|    100000|     100|0.43834976|  PASSED  
          sts_serial|   7|    100000|     100|0.82749633|  PASSED  
          sts_serial|   8|    100000|     100|0.61964056|  PASSED  
          sts_serial|   8|    100000|     100|0.84470358|  PASSED  
          sts_serial|   9|    100000|     100|0.61084638|  PASSED  
          sts_serial|   9|    100000|     100|0.89748048|  PASSED  
          sts_serial|  10|    100000|     100|0.90405441|  PASSED  
          sts_serial|  10|    100000|     100|0.79157675|  PASSED  
          sts_serial|  11|    100000|     100|0.92327079|  PASSED  
          sts_serial|  11|    100000|     100|0.54647206|  PASSED  
          sts_serial|  12|    100000|     100|0.84907989|  PASSED  
          sts_serial|  12|    100000|     100|0.99933615|   WEAK   
          sts_serial|  13|    100000|     100|0.95933739|  PASSED  
          sts_serial|  13|    100000|     100|0.78729999|  PASSED  
          sts_serial|  14|    100000|     100|0.87372021|  PASSED  
          sts_serial|  14|    100000|     100|0.76887641|  PASSED  
          sts_serial|  15|    100000|     100|0.66180754|  PASSED  
          sts_serial|  15|    100000|     100|0.94824490|  PASSED  
          sts_serial|  16|    100000|     100|0.73742918|  PASSED  
          sts_serial|  16|    100000|     100|0.40487618|  PASSED  
         rgb_bitdist|   1|    100000|     100|0.38360063|  PASSED  
         rgb_bitdist|   2|    100000|     100|0.68213811|  PASSED  
         rgb_bitdist|   3|    100000|     100|0.81934016|  PASSED  
         rgb_bitdist|   4|    100000|     100|0.98313579|  PASSED  
         rgb_bitdist|   5|    100000|     100|0.61639286|  PASSED  
         rgb_bitdist|   6|    100000|     100|0.42358300|  PASSED  
         rgb_bitdist|   7|    100000|     100|0.46974707|  PASSED  
         rgb_bitdist|   8|    100000|     100|0.05191410|  PASSED  
         rgb_bitdist|   9|    100000|     100|0.18758372|  PASSED  
         rgb_bitdist|  10|    100000|     100|0.98944016|  PASSED  
         rgb_bitdist|  11|    100000|     100|0.99659990|   WEAK   
         rgb_bitdist|  12|    100000|     100|0.07389923|  PASSED  
rgb_minimum_distance|   2|     10000|    1000|0.85926942|  PASSED  
rgb_minimum_distance|   3|     10000|    1000|0.09939864|  PASSED  
rgb_minimum_distance|   4|     10000|    1000|0.52645959|  PASSED  
rgb_minimum_distance|   5|     10000|    1000|0.12902970|  PASSED  
    rgb_permutations|   2|    100000|     100|0.50181236|  PASSED  
    rgb_permutations|   3|    100000|     100|0.17876296|  PASSED  
    rgb_permutations|   4|    100000|     100|0.60677917|  PASSED  
    rgb_permutations|   5|    100000|     100|0.51277548|  PASSED  
      rgb_lagged_sum|   0|   1000000|     100|0.07184671|  PASSED  
      rgb_lagged_sum|   1|   1000000|     100|0.50132529|  PASSED  
      rgb_lagged_sum|   2|   1000000|     100|0.22156729|  PASSED  
      rgb_lagged_sum|   3|   1000000|     100|0.51181770|  PASSED  
      rgb_lagged_sum|   4|   1000000|     100|0.15024152|  PASSED  
      rgb_lagged_sum|   5|   1000000|     100|0.51182618|  PASSED  
      rgb_lagged_sum|   6|   1000000|     100|0.14191456|  PASSED  
      rgb_lagged_sum|   7|   1000000|     100|0.56629039|  PASSED  
```

Please note that it is not a good idea to use *cryptic-sequences* as a standard pseudo-random
number generator since it is around two orders of magnitude slower that a typical one.

## Other resources

### Existing cyphers

### The FFX Mode of Operation for Format-Preserving Encryption

Authors: Mihir Bellare, Phillip Rogaway, Terence Spies

A method is presented that allows to construct a family of algorithms to encrypt data while
preserving its format, which is pretty much what the current project has achieved. The main
difference is that they are very much focused on providing a strong encryption that can be relied
upon, and their algorithm is a Feistel cypher, a cypher for which there are mathematical proofs
of its validity.

An implementation of their work would be a nice addition to the current library and it will
be interesting to compare it to its main algorithm.

[FFX paper](https://csrc.nist.gov/csrc/media/projects/block-cipher-techniques/documents/bcm/proposed-modes/ffx/ffx-spec.pdf)

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
SplitMix is a new random number generator for Haskell. It is another algorithm derived from *DotMix*. <br/>
[SplitMix paper](https://www.researchgate.net/publication/273188325_Fast_Splittable_Pseudorandom_Number_Generators)
<br/>
[SplitMix blog post](https://www.tweag.io/blog/2020-06-29-prng-test/)

#### DotMix
The original algorithm that inspired many others. <br/>
[DotMix paper](https://www.researchgate.net/publication/221643681_Deterministic_Parallel_Random-Number_Generation_for_Dynamic-Multithreading_Platforms)

### Statistical tests

#### Dieharder
One of the most well-known suite of statistical tests. Possibly a bit dated.
<br/>
[Dieharder project page](https://webhome.phy.duke.edu/~rgb/General/dieharder.php)

#### TestU01
From *l'Université de Montréal*. I studied some mathematics there, before going back to software
engineering. <br/>
[TestU01 project page](http://simul.iro.umontreal.ca/testu01/tu01.html)
<br/>
[TestU01 paper](https://www.researchgate.net/publication/278718594_TestU01)

Note that the *cryptic-sequences* cli has options to run some of the test batteries of TestU01.

#### PracRand
"PractRand (Practically Random) is a C++ library of pseudo-random number
generators (PRNGs, or just RNGs) and statistical tests for RNGs." <br/>
[PracRand project page](https://sourceforge.net/projects/pracrand/)

Note: The test suite has not been tried with *cryptic-sequences*.
