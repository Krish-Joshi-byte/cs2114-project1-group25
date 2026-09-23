# Sleep Tracker

A command-line sleep journal for recording nightly sleep, reviewing sleep
history, and identifying patterns in sleep duration. This project was created
for CS 2114.

## Features

- Create a user profile with a nightly sleep goal.
- Record bedtime, wake time, sleep quality, wake-ups, and optional notes.
- Validate dates, times, goals, quality ratings, and wake-up counts before
	storing an entry.
- View all recorded nights, including duration and weekend status.
- View all-time, weekly, and monthly sleep statistics.
- Compare weekday and weekend sleep averages.
- View longest and shortest nights and estimated sleep debt.
- Receive recommendations based on average weekday and weekend sleep.
- Change the nightly sleep goal during a session.

## Requirements

- Java 11 or newer
- A terminal or command prompt

The application uses only the Java standard library. It does not require a
database, network connection, or external runtime dependencies.

## Compile and Run

From the project directory, compile the application classes:

```bash
javac InputValidator.java SleepEntry.java SleepJournal.java User.java \
	SleepStatsCalculator.java RecommendationEngine.java SleepTrackerApp.java
```

Start the application with:

```bash
java SleepTrackerApp
```

The application is interactive. It asks for a name and sleep goal, then shows
the main menu:

1. Log a sleep entry
2. View sleep history
3. View statistics and recommendations
4. Change the sleep goal
5. Quit
6. Change the user name

Enter bedtime and wake time in `yyyy-MM-dd HH:mm` format, for example
`2026-09-21 22:30`. A wake time must be later than the bedtime. Sleep quality
uses a scale from 1 to 5, and wake-up counts must be between 0 and 50.

## Testing

The test classes use the CS 2114 `student.TestCase` framework. Run them through
the course-provided test runner or the testing setup configured by your course
environment. The test files cover validation, model classes, journal storage,
statistics, recommendations, and interactive application flows.

The application classes can be compiled without the course testing library by
using the compile command above.

## Project Structure

| Class | Responsibility |
| --- | --- |
| `SleepTrackerApp` | Console prompts, menu flow, and formatted output |
| `InputValidator` | Validation and parsing helpers |
| `User` | User name, sleep goal, and journal ownership |
| `SleepEntry` | Data for one sleep session |
| `SleepJournal` | Stores and filters sleep entries |
| `SleepStatsCalculator` | Computes averages, extremes, and sleep debt |
| `RecommendationEngine` | Generates feedback from sleep statistics |

## Data Storage

Entries are stored in memory while the program is running. Closing the
application clears the current user and all recorded entries; file or database
persistence is not currently implemented.
