#!/usr/bin/perl

use strict;

my %hash;

foreach my $line ( <STDIN> ) {
    chomp( $line );
    my @cols = split('\t',$line);
    my $key = $cols[1].",".$cols[0];
    my $index = $cols[2]-1;
    @{$hash{$key}}[$index] = $cols[3];

}

my $size = scalar(keys %hash);
my $i = 0;
foreach my $key (sort keys %hash) {
    my @array = @{$hash{$key}};
    print "{\nname: '$key',\ndata: [";
    print join(', ', @array);
    print "]\n}";

    if ($i < $size -1) {
	print ",";
    }
    $i++;
}

print "\n";
