# Chess game

This is a web-based chess game developed with Java Spring and Thymeleaf, featuring a player vs AI mode where the opponent is the Google AI Gemini.

## Technologies Used

- **Backend**: Java, Spring Boot
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Database**: MongoDB
- **HTTP Client**: OkHttp for API requests
- **Chess Logic**: powered by the [jChessify](https://github.com/puffinsoft/jchessify)
- **AI Opponent**: Google AI Gemini

## Getting Started

### Prerequisites
- Java 17
- Maven
- MongoDB 
- [Google API Key](https://makersuite.google.com/app/apikey)

### Installation
1. Clone the repository:
    ```bash
    git clone https://github.com/elkceo/chess-game.git
    cd chess-game
    ```
2. Create a `application.properties` file in `src/main/resources` with the following content:
    ```properties
    google.api.key=YOUR_GOOGLE_API_KEY
    mongodb.database.uri=YOUR_MONGODB_URI
    mongodb.database.name=YOUR_MONGODB_DATABASE_NAME
    ```
3. Build the project:
    ```bash
    mvn clean install
    ```
4. Run the project:
    ```bash
    mvn spring-boot:run
    ```
5. Open your browser and navigate to `http://localhost:8080`
6. Play the game!

## License

This project is licensed under the MIT License.
