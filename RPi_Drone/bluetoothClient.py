import sys
import bluetooth

uuid = "0a76efc0-6f89-46f3-be94-92055b259ab6"
service_matches = bluetooth.find_service( uuid = uuid )

if len(service_matches) == 0:
    print "couldn't find the FooBar service"
    sys.exit(0)

first_match = service_matches[0]
port = first_match["port"]
name = first_match["name"]
host = first_match["host"]

print "connecting to \"%s\" on %s" % (name, host)

sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
sock.connect((host, port))
sock.send("Connecting")
sock.settimeout(30)
data = sock.recv(5)
print "received [%s]" % data

sock.close()
