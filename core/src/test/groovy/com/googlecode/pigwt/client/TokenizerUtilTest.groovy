package com.googlecode.pigwt.client

import spock.lang.Specification
import com.googlecode.pigwt.client.TokenizerUtil.URLProxy

class TokenizerUtilTest extends Specification {
    def urlProxy = new URLProxy() {
        String decode(final String s) { return java.net.URLDecoder.decode(s); }
        String encode(final String s) { return java.net.URLEncoder.encode(s); }
    }

    def "params ac/dc"() {
        expect:
        string == TokenizerUtil.getParamString(params, urlProxy)
        params == TokenizerUtil.parseParams(string, urlProxy)

        where:
        string            | params
        ''                | null
        'a=b'             | ['a':'b']
        'a=b&c=d'         | ['a':'b', 'c':'d']
        'a=&=d'           | ['a':'', '':'d']
        '%26=%3D&%25=%3F' | ['&':'=', '%':'?']
        'maxRows=2'       | ['maxRows':'2']
    }

    def "parseParams edge cases"() {
        expect:
        params == TokenizerUtil.parseParams(string, urlProxy)

        where:
        string            | params
        'a'               | ['a':'']
        'a='              | ['a':'']
        '='               | [:]
    }
}
