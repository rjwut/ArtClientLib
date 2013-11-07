ArtClientLib
============

ArtClientLib is an **unofficial** Java library for developing clients to interact 
with [Artemis SBS](http://www.artemis.eochu.com/). It was originally developed
by Daniel Leong to power his (also **unofficial**) 
[Android Client](http://www.artemis.eochu.com/?page_id=28#/20121018/unofficial-android-client-for-artemis-1988927/),
created due to the lack (at the time) of an official mobile client. It was
released here on GitHub with permission of the developer of Artemis, Thom
Robertson.

This fork of the ArtClientLib project was created in order to update the project
to be compatible with Artemis 2.0. It is not intended that this branch will be
compatible with older versions of Artemis. If you need support for Artemis 1.x,
please use
[the original version of the library](https://github.com/dhleong/ArtClientLib).


# Disclaimers

ArtClientLib is completely **unofficial** and **unsupported**,with all packet
structures derived through experiment, observation, educated guesses, and luck.
Because of this, new releases of the official Artemis game are likely to break
this library until I (or some enterprising member of the community) can figure
out what changes were made and update things accordingly.

This library is made available for use on an as-is basis. I make no guarantee
of anything, and whatever you may choose to do with it is entirely your
responsibility. I request that you respect Thom Robertson and the Artemis
brand/product, and if you make something cool from this, I'd love to know.
Crediting this library would be appreciated, as would sharing any improvements
you make, to potentially include upstream contributions in the form of pull
requests. 

If you do something evil with this, though, I want no part of it!


`The remainder of this document is Daniel Leong's original instructions.`


# Getting Started

## Using the library

As mentioned below, all the objects in the world are implemented in the
`net.dhleong.acl.world` package. Just read the javadoc. The packets
which are sent and received are all somewhere under the 
`net.dhleong.acl.net` package, with the sub-packages used to group
station-specific packets.

If a packet class has only constructors which take `byte[]`s, they're
probably only "incoming" packets---that is, the client should not send them,
though in my experience the server will silently ignore packets it doesn't
understand. Sendable packets should have constructors with 
human-understandable arguments, or a factory method. Some packets may be
both sent and received, so be careful.


### Key classes/interfaces

`ArtemisNetworkInterface` - Your main interface to the library. You'll want
to create a concrete `ThreadedArtemisNetworkInterface` which, as its name
implies, runs in a background thread, leaving the main thread free to do
UI stuff, if you so choose. The methods should be pretty self-explanatory.

`OnPacketListener` - Attach implementing classes to an 
`ArtemisNetworkInterface` via `addOnPacketListener()` to be informed 
when ArtClientLib receives a packet from the server. Easy, right?

`SystemManager` - Unfortunately, a bit of a God class, this guy implements
`OnPacketListener` to create and manage all the objects in the Artemis
game world (Check out the `net.dhleong.acl.world` package)

`TestRunner` - Not really a public-facing class (at all) but it has a 
lot of simple examples of how to interact with an Artemis server; I use it
for quick testing and experimenting. 


## Contributing

There are probably many ways to reverse engineer network protocols, and I'm
by no means an expert, but these are some things I've used to reverse 
engineer Artemis' network protocol in developing this library.

* [Wireshark](http://www.wireshark.org/) - A very powerful packet sniffer
which I'm probably not using to its full potential. It is beyond the scope
if this document to explain using Wireshark, but a useful filter that I
have saved is:

```
tcp.dstport == 2010 && ip.len > 60
```

All Artemis packets you'll be interested in will be at least 60 bytes long,
and have either a destination (`dstport`) or source (`srcport`) or 2010. 
With `dstport` as above, you're looking at packets being sent to the server;
with `srcport`, you'll be looking at packets from the server.
If you're looking at incoming packets, however, it may be easier to use
one of my test classes (see below), as they will split packets logically
and print them one-per-line in an easy-to-copy format so you can examine
them in....

* [Hex Fiend](http://ridiculousfish.com/hexfiend/) - Or any other good
hex editor; this is just the one I happen to use. A good one should let 
you highlight chunks of bytes and
show you what the selected bytes mean in several formats at once. 


### Useful Classes

Another disclaimer: most of these are not really stand-alone utilities; I 
modify them as needed before running.

`RawPacketDumper` - As its name implies, if things are just going downhill
and you just want the hex. 

`PacketDemystifier` - Uses some black magic and educated guessing to attempt
to simplify the process of extrapolating what types of fields (IE: int, byte,
float, etc.) are where in a packet. Not perfect, and not very fancy (sorry,
no machine learning here!) but proved quite helpful in the transition from
1.661 to 1.700.

`test.ObjectParsingTests` - A JUnit test to make sure the 
packet parsing works as expected for all packets seen so far. If you get
a crash, it should dump the raw packet out; throw it in the appropriate
test here, and get to work! Note that any or all of these could become
invalid with new releases.

`util.ObjectParser` - Super helpful and flexible class for parsing raw packet
data into actual usable stuff. It's not very well-documented, and some of the 
terminology may be strange or misleading---it's based off my initial
assumptions, and I never felt a need to change the terms after I got a better
grasp of the packet structures---so check out the many many usages in just
about any of the incoming packets.

`net.PacketParser` - Reads in raw bytes and spits out `ArtemisPacket`s. If
you create a new packet type, you'll have to parse it out in here. This is
one of those bits of code that's very legacy and not pretty, but I haven't
had the time or drive to make it better; it works well enough for now. 
This class also has a bunch of static util methods for extracting 
little-endian (Lend) numbers from raw bytes, as well as going the other way.
For reading, though, you will probably want to be using `ObjectParser`.
