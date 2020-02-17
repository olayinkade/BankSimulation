#!/usr/bin/perl -w
use IO::Socket::INET;    
use strict;
use warnings;
$| = 1;

while(1){
  my $cliSocket = new IO::Socket::INET(
    PeerAddr => 'owl.cs.umanitoba.ca',
    PeerPort => '13359',
    Proto    => 'tcp'); 
    die "Can't create socket and connect to server" unless $cliSocket;
print    "Connected \n";
print "Enter Command \n";
  my $req = <STDIN>;
  chomp($req);
  if (($req cmp 'E') == 0){
    $cliSocket->close();
    last;
  }
  
  $cliSocket->send($req);
  shutdown($cliSocket,1);
  my $response = "";
	$cliSocket->recv($response, 1024);
  shutdown($cliSocket,0);
  print "$response\n";
  $response = "";  
}
