package com.fabioandreola.github.topcontributors.util

final class TestUtil {

    private TestUtil() {}

    final static getFileContent(String fileName) {
        def file = new File(TestUtil.class.getResource("/$fileName").toURI())
        return file.text
    }
}
