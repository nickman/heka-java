==========
heka-java
==========

heka-java is a Java client for `Heka <http://heka-docs.readthedocs.org/en/latest/>`_.

The code is in a very early state; the API is still likely to change and there are sure to be a plethora of bugs, so use this in production at your own risk.

Examples
========

Connecting to a ``hekad`` instance over UDP and sending a message::

    HekaClient hekaClient = HekaClient.udp("localhost", 4880);

    Message msg = hekaClient.message()
        .type("msg_event")
        .payload("An event from Java")
        .severity(6)
        .build();
    hekaClient.send(msg);

Connecting to multiple TCP instances (events will be sent across all connections) with JSON message encoding (instead of the default, protocol buffers)::

    Encoder encoder = new JsonEncoder();

    TcpTransport transport = new TcpTransport(encoder);
    transport.setup();
    transport.connect("host1", 5565);
    transport.connect("host2", 5565);

    HekaClient hekaClient = new HekaClient(transport, new MessageDefaults());

Configuring HMAC authentication::

    HmacConfiguration hConf = new HmacConfiguration(
                    "ops",
                    0,
                    HashFunction.SHA1,
                    "4865ey9urgkidls xtb0[7lf9rzcivthkm");

    Encoder encoder = new ProtobufEncoder();
    encoder.setHmacConfiguration(hConf);

    HekaClient hekaClient = HekaClient.tcp("localhost", 5565, encoder);

Setting default values for messages::

    MessageDefaults defaults = new MessageDefaults();
    defaults.setLogger("java_logger");
    defaults.setSeverity(3);

    hekaClient.setDefaults(defaults);

Filtering messages::

    Filter maxSeverityFilter = new MaxSeverityFilter(5);
    hekaClient.addFilter(maxSeverityFilter);

    Filter blacklistFilter = new TypeBlacklistFilter();
    blacklistFilter.add("ignore_msg");
    blacklistFilter.add("debug");
    hekaClient.addFilter(blacklistFilter);

Credits
=======

The API design was inspired by `riemann-java-client <https://github.com/aphyr/riemann-java-client>`_.
