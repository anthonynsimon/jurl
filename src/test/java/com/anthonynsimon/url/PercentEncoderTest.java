package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;
import org.junit.Assert;
import org.junit.Test;

public class PercentEncoderTest {

    private EncodingTestCase[] escapeCases = new EncodingTestCase[]{
            new EncodingTestCase("", ""),
            new EncodingTestCase("onetwothree", "onetwothree"),
            new EncodingTestCase("ÿ", "%C3%BF"),
            new EncodingTestCase("ÿ/com", "%C3%BF%2Fcom"),
            new EncodingTestCase("one&two three", "one%26two%20three"),
            new EncodingTestCase("à á â ã ä å æ ç è é ê ë ì í î ï ", "%C3%A0%20%C3%A1%20%C3%A2%20%C3%A3%20%C3%A4%20%C3%A5%20%C3%A6%20%C3%A7%20%C3%A8%20%C3%A9%20%C3%AA%20%C3%AB%20%C3%AC%20%C3%AD%20%C3%AE%20%C3%AF%20"),
            new EncodingTestCase("a50%", "a50%25"),
            new EncodingTestCase(" ?&=#+%!<>#\"{}|\\^[]`☺\t:/@$'()*,;", "%20%3F%26%3D%23%2B%25%21%3C%3E%23%22%7B%7D%7C%5C%5E%5B%5D%60%E2%98%BA%09%3A%2F%40%24%27%28%29%2A%2C%3B"),
            new EncodingTestCase("ϣ%Ϧ", "%CF%A3%25%CF%A6"),
            new EncodingTestCase("Îºá½¹ÏƒÎ¼Îµ", "%C3%8E%C2%BA%C3%A1%C2%BD%C2%B9%C3%8F%C6%92%C3%8E%C2%BC%C3%8E%C2%B5"),
            new EncodingTestCase("þ", "%C3%BE"),
            new EncodingTestCase("𡺸", "%F0%A1%BA%B8"),
            new EncodingTestCase("ü€€€€€", "%C3%BC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC"),
            new EncodingTestCase("𡺸 𡺹 𡺺", "%F0%A1%BA%B8%20%F0%A1%BA%B9%20%F0%A1%BA%BA"),
            new EncodingTestCase("𡺸 𡺹 𡺺", "%F0%A1%BA%B8%20%F0%A1%BA%B9%20%F0%A1%BA%BA"),
            new EncodingTestCase(" 𩕵 𩕶 𩕷 𩕸 𩕹 𩕺 𩕻 𩕼 𩕽 𩕾 𩕿 𩖀 𩖁 𩖂 𩖃 𩖄 𩖅 𩖆 𩖇 𩖈 𩖉 𩖊 𩖋 𩖌 𩖍 𩖎 𩖏 𩖐 ", "%20%F0%A9%95%B5%20%F0%A9%95%B6%20%F0%A9%95%B7%20%F0%A9%95%B8%20%F0%A9%95%B9%20%F0%A9%95%BA%20%F0%A9%95%BB%20%F0%A9%95%BC%20%F0%A9%95%BD%20%F0%A9%95%BE%20%F0%A9%95%BF%20%F0%A9%96%80%20%F0%A9%96%81%20%F0%A9%96%82%20%F0%A9%96%83%20%F0%A9%96%84%20%F0%A9%96%85%20%F0%A9%96%86%20%F0%A9%96%87%20%F0%A9%96%88%20%F0%A9%96%89%20%F0%A9%96%8A%20%F0%A9%96%8B%20%F0%A9%96%8C%20%F0%A9%96%8D%20%F0%A9%96%8E%20%F0%A9%96%8F%20%F0%A9%96%90%20"),
            new EncodingTestCase("www.Ǌǋ ǭǮǯǰǱǲ௲ԏ ԱԲԳԴԵ௳௴௵ ǳ Ǵ ǵ Ƕ ǷǸ ǹǌǍ.com/^path?", "www.%C7%8A%C7%8B%20%C7%AD%C7%AE%C7%AF%C7%B0%C7%B1%C7%B2%E0%AF%B2%D4%8F%20%D4%B1%D4%B2%D4%B3%D4%B4%D4%B5%E0%AF%B3%E0%AF%B4%E0%AF%B5%20%C7%B3%20%C7%B4%20%C7%B5%20%C7%B6%20%C7%B7%C7%B8%20%C7%B9%C7%8C%C7%8D.com%2F%5Epath%3F"),
    };

    private EncodingTestCase[] unescapeCases = new EncodingTestCase[]{
            new EncodingTestCase("", ""),
            new EncodingTestCase("www.example.com/%20%DFhei", "www.example.com/ �hei"),
            new EncodingTestCase("www.example.com/%20%C3%BF%20", "www.example.com/ ÿ "),
            new EncodingTestCase("www.example.com/%20%c3%bF%20", "www.example.com/ ÿ "),
            new EncodingTestCase("one%26two%20three", "one&two three"),
            new EncodingTestCase("www.example.com", "www.example.com"),
            new EncodingTestCase("http://test.%C7%B7%C7%B8%20%C7%B9%C7%8C%C7%8D.com/foo", "http://test.ǷǸ ǹǌǍ.com/foo"),
            new EncodingTestCase("http%3a%2F%2F%E0%AF%B5.com", "http://௵.com"),
            new EncodingTestCase("_%25_~_--_/_", "_%_~_--_/_"),
            new EncodingTestCase("_______", "_______"),
            new EncodingTestCase("%20%3F%26%3D%23%2B%25%21%3C%3E%23%22%7B%7D%7C%5C%5E%5B%5D%60%E2%98%BA%09%3A%2F%40%24%27%28%29%2A%2C%3B", " ?&=#+%!<>#\"{}|\\^[]`☺\t:/@$'()*,;"),
            new EncodingTestCase("%C3%BC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC%E2%82%AC", "ü€€€€€"),
            new EncodingTestCase("www.%C7%8A%C7%8B%20%C7%AD%C7%AE%C7%AF%C7%B0%C7%B1%C7%B2%E0%AF%B2%D4%8F%20%D4%B1%D4%B2%D4%B3%D4%B4%D4%B5%E0%AF%B3%E0%AF%B4%E0%AF%B5%20%C7%B3%20%C7%B4%20%C7%B5%20%C7%B6%20%C7%B7%C7%B8%20%C7%B9%C7%8C%C7%8D.com%2F%5Epath%3F", "www.Ǌǋ ǭǮǯǰǱǲ௲ԏ ԱԲԳԴԵ௳௴௵ ǳ Ǵ ǵ Ƕ ǷǸ ǹǌǍ.com/^path?"),
    };

    @Test
    public void testEscaping() throws Exception {
        for (EncodingTestCase testCase : escapeCases) {
            Assert.assertEquals(testCase.expectedOutput, PercentEncoder.encode(testCase.input, URLPart.ENCODE_ZONE));
        }
    }

    @Test
    public void testUnescaping() throws Exception {
        for (EncodingTestCase testCase : unescapeCases) {
            Assert.assertEquals(testCase.expectedOutput, PercentEncoder.decode(testCase.input));
        }
    }

    @Test(expected = MalformedURLException.class)
    public void testUnescapeInvalidHex() throws Exception {
        PercentEncoder.decode("http://www.domain.com/path%C3%##");
    }

    @Test(expected = MalformedURLException.class)
    public void testMalformedUnescape() throws Exception {
        PercentEncoder.decode("_______%");
    }

    @Test(expected = MalformedURLException.class)
    public void testMalformedUnescapeWithUnicodeMultiple() throws Exception {
        PercentEncoder.decode("!__!__%Ƿabasda");
    }

    @Test(expected = MalformedURLException.class)
    public void testMalformedUnescapeWithUnicodeSingle() throws Exception {
        PercentEncoder.decode("!__!__%Ƿ");
    }

    class EncodingTestCase {
        public String input;
        public String expectedOutput;

        public EncodingTestCase(String input, String expectedOutput) {
            this.input = input;
            this.expectedOutput = expectedOutput;
        }
    }
}
