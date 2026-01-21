# Cocktail Explorer

An Android application for exploring and managing cocktail recipes.

## Features

- **Browse Cocktails** - View a collection of cocktail recipes with images, ingredients, and instructions
- **Search & Filter** - Search cocktails by name and filter by category
- **Favorites** - Save your favorite cocktails for quick access
- **Swipe to Delete** - Remove cocktails with a simple swipe gesture
- **Notifications** - Get notified when adding favorites and set cocktail reminders
- **Multi-language Support** - Available in English and Croatian

## Tech Stack

- **Language**: Kotlin
- **Architecture**: Fragment-based navigation with ContentProvider
- **Database**: SQLite with CursorLoader
- **Networking**: Retrofit for API calls
- **Background Work**: WorkManager for data synchronization
- **UI**: Material Design components, RecyclerView with ListAdapter

## Project Structure

```
app/src/main/java/hr/algebra/cocktailexplorer/
├── adapter/          # RecyclerView adapters
├── api/              # API service and data fetching
├── data/             # Database, repositories, and models
├── fragment/         # UI fragments
├── framework/        # Extension functions
├── models/           # Data classes
└── notification/     # Notification and alarm handling
```

## Permissions

- `INTERNET` - Fetch cocktail data from API
- `POST_NOTIFICATIONS` - Send notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM` - Schedule cocktail reminders
- `RECEIVE_BOOT_COMPLETED` - Restore alarms after device restart

## Building

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device (min SDK 24)

## License

This project is for educational purposes only.

##Demo:
[link](url:https://algebrapou-my.sharepoint.com/personal/ikamber_algebra_hr/_layouts/15/stream.aspx?id=%2Fpersonal%2Fikamber%5Falgebra%5Fhr%2FDocuments%2FPrivici%2FVID%5F20260121%5F220129%2Emp4&ct=1769032755449&or=OWA%2DNT%2DMail&cid=55545886%2D21e9%2D8916%2D39cf%2De2fae3323dc4&ga=1&referrer=StreamWebApp%2EWeb&referrerScenario=AddressBarCopied%2Eview%2Eeacec5e1%2D589c%2D4ace%2D9180%2Df185252777b7)

![gif-demo-cocktail-explorer](https://github.com/user-attachments/assets/92b7d92a-3dda-44cb-85a1-029bf846dbbc)


