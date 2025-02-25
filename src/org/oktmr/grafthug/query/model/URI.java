package org.oktmr.grafthug.query.model;

public class URI extends Resource {
    private int namespaceIdx;

    public URI(String uri) {
        super(uri);
        this.namespaceIdx = getLocalNameIndex(uri);
    }

    /**
     * Detects the index where the localname ends
     *
     * @param uri the uri to detect
     * @return the index where the type needs to be splitted
     */
    private static int getLocalNameIndex(String uri) {
        int separatorIdx = uri.indexOf('#');

        if (separatorIdx < 0) {
            separatorIdx = uri.lastIndexOf('/');
        }

        if (separatorIdx < 0) {
            separatorIdx = uri.lastIndexOf(':');
        }

        if (separatorIdx < 0) {
            throw new IllegalArgumentException("No separator character founds in URI: " + uri);
        }

        return separatorIdx + 1;
    }

    /**
     * Removes &lt; and &gt;
     *
     * @param namespace given String to clean
     * @return cleaned result
     */
    public static String cleanAngleQuotes(String namespace) {
        if (namespace.charAt(0) == '<' && namespace.charAt(namespace.length() - 1) == '>')
            return namespace.substring(1, namespace.length() - 1);

        return namespace;
    }

    @Override
    public String stringValue() {
        return name;
    }
}
