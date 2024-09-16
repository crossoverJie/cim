package com.crossoverjie.cim.client.sdk;

public interface Event {
    void debug(String msg, Object... replace);
    void info(String msg, Object... replace);
    void warn(String msg, Object... replace);
    void error(String msg, Object... replace);
    void fatal(Client client);

    class DefaultEvent implements Event {
        @Override
        public void debug(String msg, Object... replace) {
            System.out.println(msg);
        }

        @Override
        public void info(String msg, Object... replace) {
            System.out.println(msg);
        }

        @Override
        public void warn(String msg, Object... replace) {
            System.out.println(msg);
        }

        @Override
        public void error(String msg, Object... replace) {
            System.err.println(msg);
        }

        @Override
        public void fatal(Client client) {

        }
    }
}
