# Movie Ticket Booking System

An object-oriented implementation of a movie ticket booking system in Java with concurrency handling.

## Architecture

- **Domain Model**: Movie, Theatre, Screen, Seat, Show, ShowSeat, Booking, Payment
- **Repository Layer**: In-memory stores for theatres, shows, and bookings
- **Service Layer**: `ShowService` (concurrency core) and `BookingService` (API layer)
- **Concurrency**: Per-show `ReentrantLock` for booking, `synchronized` for admin show-addition

## APIs

| Method | Description |
|---|---|
| `bookTickets(showId, seatIds, userId)` | Books seats atomically, returns Booking |
| `showTheatres(city)` | Lists theatres in a city |
| `showMovies(city)` | Lists unique movies playing in a city |
| `cancelBooking(bookingId)` | Cancels booking and processes refund |

## How to Run

```bash
cd movie-ticket-booking
javac *.java
java Main
```
