
# Koinvert

## Features
- **Currency Conversion**: Feel free to type in the amount needed to convert and select the currencies you want to convert from and to.
- **Latest Exchange Rates**: Ensures you the latest exchange rates from the Exchange Rates API. (https://exchangeratesapi.io/)
- **Country Information**: Better visualization with a list of currencies with flags and currencies' name using the REST Countries API. (https://restcountries.com/v3.1/all) and (https://cdnjs.com/libraries/flag-icons)
- **Search**: Search for a currency by name or currency ISO code at ease.
- **Responsive Design**: The app is designed to work on both portrait and landscape orientations with the help of ssp and sdp libraries.

## App Structure

- **MVVM Architecture**: Uses the Model-View-ViewModel architectural pattern for separation of concerns.
   - **Repository**: Contains logic to fetch data from `CountryApiService` and `CurrencyApiService`, handling both success and error cases.
   - **ViewModel**: Provides data to the UI and manages API calls with the use of `LiveData` to observe data changes.
- **Networking**: Retrofit2 is used for making API calls.

## Steps to Build and Run the App

1. **Clone the repository**:
   ```bash
   git clone https://github.com/kuwelym/Koinvert.git
   ```
2. **Open the project** in Android Studio.
3. **Add API Key**:
   - Obtain an API key from [Exchange Rates API](https://exchangeratesapi.io/).
   - Set the API key in your environment as `ExchangeRatesApiKey` or update it directly in `defaultConfig` in `build.gradle`.
4. **Build the project**:
   - Sync the project with Gradle files.
5. **Run the app** on an emulator or connected device.

## Additional Notes

- **Data Binding and View Binding**: The app uses both Data Binding and View Binding for efficient UI updates and reduced boilerplate code.
- **Challenges**:
  - Solving the issues with internet connection availability and handling API errors was a tricky part of the project.
  
## Demo

[Link to Video Demonstration](https://youtube.com/shorts/g8U6lUj0ltI)
