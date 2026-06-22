## Final Project Description

Create store application. Design your API in RESTful manner. In the examples you get samples of JSONs that your API should receive or send.

Your store has to support following methods:

1. Register new user. Example request: {"email":"my@email.com", "password":"123"}
Respond with an appropriate HTTP codes (200 for ok, 409 for existing user)
Your app must not store password as plain text, use some good approach to identify user.

2. Login into system. Example request: {"email":"my@email.com", "password":"123"}
Respond with JSON containing sessionId.

3. Get all products in store.
Respond with JSON list of items you have, e.g.:
{"id":"2411", "title":"Nail gun", "available":8, "price": "23.95"}

4. Add item to cart. Example request: {"id":"363", "quantity":"2"}
Allow adding only one position at a time. If you don’t have this quantity in store - respond with an error. The information has to be session-scoped: once session expires - user will get new empty cart.

5. Display your cart content.
Respond with list of product names with their quantities added. Calculate subtotal. Assign an ordinal to each cart item.

6. Remove an item from user’s cart.

7. Modify cart item. Example request: {"id":2, quantity: 3} - user should be able to modify number of some items in his cart.

8. Checkout: verify your prices in cart, ensure you still have desired amount of goods. If all is good - send a user confirmation about successful order.

## Database Environment Setup with Docker Compose

The application uses a PostgreSQL database managed via Docker Compose for an isolated and reproducible environment.

To start the database container in the background, run the following command in the directory containing the `docker-compose.yml` file:

docker-compose up -d

Make sure your `application.properties` file uses the following configuration:
spring.datasource.url=jdbc:postgresql://localhost:5432/market_db
spring.datasource.username=market_user
spring.datasource.password=market_password

To stop the database, run:
docker-compose down

## API Documentation with Swagger

Interactive API documentation is automatically generated using Swagger UI.

When the application is running, the interactive documentation is accessible at:

http://localhost:8080/swagger-ui/index.html

