# Base Ktor Server (BKS)
BKS is a base project used to create new servers for Wokis network.

## Index
* [Pre-requisites](https://github.com/Wikijito7/ECIBot#pre-requisites)
* [How to run it](https://github.com/Wikijito7/ECIBot#how-to-run-it)
* [How it works](https://github.com/Wikijito7/ECIBot#how-it-works)
* [TODO](https://github.com/Wikijito7/ECIBot#todo)

## Pre-requisites
* Java 17.
* Other requisites are already built in on `build.gradle.kts`. ([See more here](https://github.com/Wikijito7/base-ktor-server/blob/master/build.gradle.kts))
* Knowledge using a terminal.
* A little bit of time.
* Optional: Coffee to drink while executing the app.

## How to run it
- After checking pre-requisites and having all dependencies installed, you can just go to `Application.kt` and run `fun main()`.
- Also, you can generate a `fat-jar` by executing `fatJar` task on `build.gradle.kts`. After that, you may execute the app by double clicking it or by executing it on a terminal using `java -jar base-ktor-server.jar`.

## How it works
BKS is a base project made to be used on other projects for Wokis network. It has already user related routing and ready to use.

You may configure some stuff on `app.conf` file. You can check it out [here](https://github.com/Wikijito7/base-ktor-server/blob/master/src/main/resources/app.conf).

### Routing
Routes are rate limited. They can be used up to 100 times by default, but they may be modified to change this limitation.
#### User routing

##### Not authenticated
- `GET /user/{id}/avatar`: get user's avatar, if it exists.

##### Authenticated
- `GET /users`: fetch all users on the database. It may be deleted on other project.
- `GET /user`: fetch current user's info. It identifies the user by using JWT token. It will only fetch its own info.
- `PUT /user`: updates current user. It identifies the user by using JWT token. It will only modify its own info.
- `POST /user/image`: uploads new image for the authenticated user. It will be saved on the folder configured on `app.conf`.
- `DELETE /user/image`: removes user's image.
- `POST: /2fa`: activates 2fa authentication on this account. It will be used to confirm actions. 2fa will be asked whenever the route has `withAuthenticator(user)` wrapper.
- `DELETE /2fa`: removes 2fa authentication on this account. It will ask 2fa code.

#### Auth routing
It is rate limited using a custom rate limit. This routes can be used up to 10 times a minute.
##### Not authenticated
- `POST /login`: logs in the user. It can use username or email to authenticate.
- `POST /register`: registers the user. It will be asked username, email and password.
- `GET /verify/{token}`: It's used to verify user's email. It may be skipped, but is recommended to verify user's email.
- `POST /recover`: Used to generate recover email to recover user's password.
- `POST /recover-pass`: It will be used to recover user's password.

##### Authenticated
- `POST /verify`: resends verification email.
- `POST /change-pass`: changes user's password. It need old password and new password.
- `POST /logout`: removes user token. 
- `DELETE /sessions`: removes all user tokens.

## TODO:
Any suggestion? Create a ticket [here](https://github.com/Wikijito7/base-ktor-server/issues).

## Known bugs
* Have you found one? Create a ticket [here](https://github.com/Wikijito7/base-ktor-server/issues).
