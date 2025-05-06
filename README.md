# 🌎 ZIP Code Processor

![ZIP Code Processor Banner](https://img.shields.io/badge/ZIP%20Code-Processor-blue?style=for-the-badge&logo=java)

A powerful Java application that processes US ZIP codes to retrieve and store geographical information using the [Zippopotam.us API](https://api.zippopotam.us/).

## ✨ Features

- 🔍 **Single ZIP Code Processing**: Enter a single ZIP code to retrieve its geographical data
- 📋 **Batch Processing**: Process multiple ZIP codes from a text file
- 📊 **CSV Export**: Automatically exports valid ZIP code data to a CSV file
- 📝 **Error Logging**: Logs invalid ZIP codes with detailed error messages
- 🌐 **API Integration**: Seamlessly integrates with the Zippopotam.us API

## 📋 Requirements

- Java 8 or higher
- Internet connection (for API access)

## 🚀 Getting Started

### Installation

1. Clone this repository:
   ```
   git clone https://github.com/kamkode/FlipKart_Interview_Task.git
   ```
2. Navigate to the project directory:
   ```
   cd FlipKart_Interview_Task
   ```
3. Compile the Java file:
   ```
   javac ZipCodeProcessor.java
   ```

### Usage

#### Running the Application

```
java ZipCodeProcessor
```

#### Options

The application provides two modes of operation:

1. **Single ZIP Code Mode**: Enter a single ZIP code when prompted
2. **Batch Processing Mode**: Process multiple ZIP codes from the `zipcodes.txt` file

#### Input File Format

For batch processing, create a `zipcodes.txt` file with one ZIP code per line:

```
33160
90210
10001
85001
```

#### Output Files

- **zip_data.csv**: Contains valid ZIP code data with the following columns:
  - ZIP Code
  - Country
  - Place Name
  - State
  - State Abbreviation
  - Latitude
  - Longitude

- **invalid_zipcodes.txt**: Logs invalid ZIP codes with error messages

## 📊 Example Output

### Valid ZIP Code Data (zip_data.csv)

```
ZIP Code,Country,Place Name,State,State Abbreviation,Latitude,Longitude
90210,United States,Beverly Hills,California,CA,34.0901,-118.4065
10001,United States,New York,New York,NY,40.7143,-74.0060
```

### Invalid ZIP Code Log (invalid_zipcodes.txt)

```
99999 - Not found (404)
00000 - Not found (404)
```

## 🛠️ Technologies Used

- **Java**: Core programming language
- **HttpURLConnection**: For API requests
- **Regular Expressions**: For JSON parsing
- **File I/O**: For reading and writing files
- **Zippopotam.us API**: External API for ZIP code data

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/kamkode/FlipKart_Interview_Task/issues).

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/kamkode">kamkode</a>
</p>