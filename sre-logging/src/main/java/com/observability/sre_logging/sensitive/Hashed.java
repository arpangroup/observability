package com.observability.sre_logging.sensitive;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public @interface Hashed {
    String ALGORITHM_CONFIG= "algorithm";

    /**
     * The hash algorithm to use, defaults to {@link Algorithm#SHA_256}
     *
     * @return hashing algorithm configured.
     */
    Algorithm value() default Algorithm.SHA_256;

    enum Algorithm {
        SHA_256("SHA-256", Hashing.sha256()),
        SHA_384("SHA-384", Hashing.sha384()),
        SHA_512("SHA-512", Hashing.sha512()),
        MURMUR_3("MURMUR3", Hashing.murmur3_128());

        private final String jdkAlgorithm;
        private final HashFunction hashFunction;

        Algorithm (String jdkAlgorithm, HashFunction function) {
            this.jdkAlgorithm = jdkAlgorithm;
            this.hashFunction = function;
        }
    }
    public String getJdkAlgorithm() {
        return this.jdkAlgorithm;
    }

    public HashFunction getHashFunction() {
        return this.hashFunction;
    }
}
