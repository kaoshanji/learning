package com.sun.corba.se.PortableActivationIDL.LocatorPackage;


/**
* com/sun/corba/se/PortableActivationIDL/LocatorPackage/ServerLocationPerORB.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /HUDSON/workspace/8-2-build-linux-amd64/jdk8u231/13620/corba/src/share/classes/com/sun/corba/se/PortableActivationIDL/activation.idl
* Saturday, October 5, 2019 3:01:48 AM PDT
*/

public final class ServerLocationPerORB implements org.omg.CORBA.portable.IDLEntity
{
  public String hostname = null;
  public com.sun.corba.se.PortableActivationIDL.EndPointInfo ports[] = null;

  public ServerLocationPerORB ()
  {
  } // ctor

  public ServerLocationPerORB (String _hostname, com.sun.corba.se.PortableActivationIDL.EndPointInfo[] _ports)
  {
    hostname = _hostname;
    ports = _ports;
  } // ctor

} // class ServerLocationPerORB