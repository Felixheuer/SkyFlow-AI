'use client';

import { useEffect, useState } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { getBookings, Booking } from '@/lib/api';
import { 
  Database, 
  Search, 
  CheckCircle, 
  XCircle, 
  Calendar,
  Plane,
  User as UserIcon,
  Filter,
  RefreshCw
} from 'lucide-react';
import { Button } from '@/components/ui/button';

interface BookingsTableProps {
  refreshTrigger?: number;
}

export function BookingsTable({ refreshTrigger }: BookingsTableProps) {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [filteredBookings, setFilteredBookings] = useState<Booking[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'CONFIRMED' | 'CANCELLED'>('ALL');

  const fetchBookings = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await getBookings();
      setBookings(data);
      setFilteredBookings(data);
    } catch (err) {
      setError('Failed to load bookings. Please try again.');
      console.error('Error fetching bookings:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchBookings();
  }, [refreshTrigger]);

  useEffect(() => {
    let filtered = [...bookings];

    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(b => b.bookingStatus === statusFilter);
    }

    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      filtered = filtered.filter(b =>
        b.bookingNumber.toLowerCase().includes(query) ||
        b.firstName.toLowerCase().includes(query) ||
        b.lastName.toLowerCase().includes(query) ||
        b.from.toLowerCase().includes(query) ||
        b.to.toLowerCase().includes(query)
      );
    }

    setFilteredBookings(filtered);
  }, [searchQuery, statusFilter, bookings]);

  const getBookingClassColor = (bookingClass: string) => {
    switch (bookingClass) {
      case 'BUSINESS':
        return 'bg-primary/15 dark:bg-primary/25 text-primary border-primary/30';
      case 'PREMIUM_ECONOMY':
        return 'bg-info/15 dark:bg-info/25 text-info border-info/30';
      case 'ECONOMY':
        return 'glass border-border/50';
      default:
        return 'glass border-border/50';
    }
  };

  const formatBookingClass = (bookingClass: string) => {
    return bookingClass.replace('_', ' ');
  };

  return (
    <Card className="h-full surface-panel flex flex-col overflow-hidden">
      <CardHeader className="border-b border-border/40 bg-gradient-to-r from-card/50 to-muted/20 backdrop-blur-xl py-4 px-4 md:px-6">
        <div className="flex flex-col gap-3 md:gap-4">
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center gap-2 md:gap-3 text-base md:text-lg">
              <div className="w-8 h-8 md:w-10 md:h-10 rounded-xl gradient-primary shadow-glow flex items-center justify-center flex-shrink-0">
                <Database className="h-4 w-4 md:h-5 md:w-5 text-primary-foreground" />
              </div>
              <div className="min-w-0">
                <div className="text-base md:text-lg font-semibold truncate">Bookings Database</div>
                <div className="text-xs md:text-sm font-normal text-muted-foreground truncate">
                  {filteredBookings.length} of {bookings.length} bookings
                </div>
              </div>
            </CardTitle>
            
            <Button
              variant="outline"
              size="sm"
              onClick={fetchBookings}
              disabled={isLoading}
              className="gap-1.5 md:gap-2 glass border-border/50 hover:shadow-glow transition-all duration-300 ease-in-out flex-shrink-0 text-xs md:text-sm"
            >
              <RefreshCw className={`h-3.5 w-3.5 md:h-4 md:w-4 ${isLoading ? 'animate-spin' : ''}`} />
              <span className="hidden sm:inline">Refresh</span>
            </Button>
          </div>
          
          <div className="flex flex-col sm:flex-row gap-2 md:gap-3">
            <div className="relative flex-1">
              <Search className="absolute left-2.5 md:left-3 top-1/2 -translate-y-1/2 h-3.5 w-3.5 md:h-4 md:w-4 text-muted-foreground" />
              <Input
                placeholder="Search by booking number, name, or airport..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-8 md:pl-10 focus-ring border-border/50 glass h-9 md:h-10 lg:h-11 text-sm"
              />
            </div>
            
            <div className="flex gap-1.5 md:gap-2 overflow-x-auto pb-1">
              {(['ALL', 'CONFIRMED', 'CANCELLED'] as const).map((status) => (
                <Button
                  key={status}
                  variant={statusFilter === status ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setStatusFilter(status)}
                  className={`gap-1 md:gap-1.5 transition-all duration-300 ease-in-out text-xs whitespace-nowrap flex-shrink-0 ${
                    statusFilter === status 
                      ? 'gradient-primary text-primary-foreground border-0' 
                      : 'glass border-border/50 hover:shadow-glow'
                  }`}
                >
                  {status === 'CONFIRMED' && <CheckCircle className="h-3 w-3" />}
                  {status === 'CANCELLED' && <XCircle className="h-3 w-3" />}
                  {status === 'ALL' && <Filter className="h-3 w-3" />}
                  <span className="hidden xs:inline md:inline">{status}</span>
                </Button>
              ))}
            </div>
          </div>
        </div>
      </CardHeader>
      
      <CardContent className="p-0 flex-1 overflow-hidden bg-gradient-to-b from-background/50 to-muted/10 min-h-0">
        {isLoading ? (
          <div className="flex flex-col items-center justify-center h-full gap-3 p-6 md:p-8">
            <div className="w-10 h-10 md:w-12 md:h-12 loading-spinner" />
            <p className="text-xs md:text-sm text-muted-foreground">Loading bookings...</p>
          </div>
        ) : error ? (
          <div className="flex flex-col items-center justify-center h-full gap-3 p-6 md:p-8">
            <div className="w-10 h-10 md:w-12 md:h-12 rounded-full bg-destructive/10 dark:bg-destructive/20 flex items-center justify-center">
              <XCircle className="h-5 w-5 md:h-6 md:w-6 text-destructive" />
            </div>
            <p className="text-xs md:text-sm text-destructive text-center">{error}</p>
            <Button onClick={fetchBookings} variant="outline" size="sm" className="glass border-border/50 text-xs md:text-sm">
              Try Again
            </Button>
          </div>
        ) : (
          <div className="overflow-auto h-full scrollbar-thin">
            <Table>
              <TableHeader className="sticky top-0 glass-strong backdrop-blur-xl z-10 border-b border-border/50">
                <TableRow className="hover:bg-muted/30">
                  <TableHead className="font-semibold text-xs md:text-sm">
                    <span className="hidden sm:inline">Booking #</span>
                    <span className="sm:hidden">#</span>
                  </TableHead>
                  <TableHead className="font-semibold text-xs md:text-sm">
                    <div className="flex items-center gap-1.5 md:gap-2">
                      <UserIcon className="h-3.5 w-3.5 md:h-4 md:w-4 flex-shrink-0" />
                      <span className="hidden md:inline">Customer</span>
                      <span className="md:hidden">Name</span>
                    </div>
                  </TableHead>
                  <TableHead className="font-semibold text-xs md:text-sm hidden lg:table-cell">
                    <div className="flex items-center gap-1.5 md:gap-2">
                      <Calendar className="h-3.5 w-3.5 md:h-4 md:w-4" />
                      Date
                    </div>
                  </TableHead>
                  <TableHead className="font-semibold text-xs md:text-sm">
                    <div className="flex items-center gap-1.5 md:gap-2">
                      <Plane className="h-3.5 w-3.5 md:h-4 md:w-4 flex-shrink-0" />
                      <span className="hidden sm:inline">Route</span>
                    </div>
                  </TableHead>
                  <TableHead className="font-semibold text-center text-xs md:text-sm">Status</TableHead>
                  <TableHead className="font-semibold text-xs md:text-sm hidden xl:table-cell">Class</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredBookings.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} className="text-center py-8 md:py-12">
                      <div className="flex flex-col items-center gap-2 md:gap-3">
                        <div className="w-10 h-10 md:w-12 md:h-12 rounded-full glass flex items-center justify-center">
                          <Search className="h-5 w-5 md:h-6 md:w-6 text-muted-foreground" />
                        </div>
                        <p className="text-xs md:text-sm text-muted-foreground">
                          {searchQuery || statusFilter !== 'ALL' 
                            ? 'No bookings match your search'
                            : 'No bookings found'}
                        </p>
                      </div>
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredBookings.map((booking, index) => (
                    <TableRow 
                      key={booking.bookingNumber} 
                      className="border-border/20 hover:bg-muted/30 transition-all duration-300 ease-in-out animate-fade-in group"
                      style={{ animationDelay: `${index * 30}ms` }}
                    >
                      <TableCell className="font-mono text-xs md:text-sm font-semibold text-primary">
                        <span className="hidden sm:inline">{booking.bookingNumber}</span>
                        <span className="sm:hidden">{booking.bookingNumber.slice(-4)}</span>
                      </TableCell>
                      <TableCell className="text-xs md:text-sm">
                        <div className="flex flex-col min-w-0">
                          <span className="font-medium truncate">{booking.firstName} {booking.lastName}</span>
                        </div>
                      </TableCell>
                      <TableCell className="text-xs md:text-sm text-muted-foreground hidden lg:table-cell">
                        {new Date(booking.date).toLocaleDateString('en-US', {
                          month: 'short',
                          day: 'numeric',
                          year: 'numeric',
                        })}
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-1 md:gap-2">
                          <Badge variant="outline" className="font-mono text-[10px] md:text-xs font-semibold glass border-border/50">
                            {booking.from}
                          </Badge>
                          <Plane className="h-2.5 w-2.5 md:h-3 md:w-3 text-muted-foreground group-hover:text-primary transition-all duration-300 ease-in-out flex-shrink-0" />
                          <Badge variant="outline" className="font-mono text-[10px] md:text-xs font-semibold glass border-border/50">
                            {booking.to}
                          </Badge>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex justify-center">
                          <Badge 
                            variant={booking.bookingStatus === 'CONFIRMED' ? 'default' : 'destructive'}
                            className={`gap-0.5 md:gap-1 shadow-soft text-[10px] md:text-xs ${
                              booking.bookingStatus === 'CONFIRMED'
                                ? 'badge-success'
                                : 'badge-error'
                            }`}
                          >
                            {booking.bookingStatus === 'CONFIRMED' ? (
                              <CheckCircle className="h-2.5 w-2.5 md:h-3 md:w-3" />
                            ) : (
                              <XCircle className="h-2.5 w-2.5 md:h-3 md:w-3" />
                            )}
                            <span className="hidden sm:inline">{booking.bookingStatus}</span>
                            <span className="sm:hidden">{booking.bookingStatus === 'CONFIRMED' ? 'OK' : 'X'}</span>
                          </Badge>
                        </div>
                      </TableCell>
                      <TableCell className="hidden xl:table-cell">
                        <Badge 
                          variant="outline"
                          className={`${getBookingClassColor(booking.bookingClass)} font-medium shadow-soft text-xs`}
                        >
                          {formatBookingClass(booking.bookingClass)}
                        </Badge>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
