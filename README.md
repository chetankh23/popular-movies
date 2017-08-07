# Movie Tube Android App

`Movie Tube` is an Android app which helps you to discover the latest popular and top rated movies. You can flip through movie posters, check movie details, watch movie trailers, read other people's reviews and create a list of your favourite movies.

## Screenshots

<img width="450" align="middle" src="https://user-images.githubusercontent.com/14648407/28450548-cacf8a92-6d9c-11e7-88b4-e5d772857dda.png" alt="Popular Movies Screen">

<img width="450" src="https://user-images.githubusercontent.com/14648407/28450547-cacf1a30-6d9c-11e7-82cc-fa49aabfce56.png" alt="Movie Detail Screen">

<img width="450" src="https://user-images.githubusercontent.com/14648407/28450550-cae179b4-6d9c-11e7-98a5-a8b27ea274a4.png" alt="Movie Detail Screen">

## Build

The app retrieves movie data from MovieDB and Youtube video thumbnail using Youtube's default URL. To build the project, please add the following API key to the environment variable on your build machine.

* MOVIEDB_API_KEY

## Data Retrieval Logic

This app retrieves a Movie list using a Async Task. The Async Task performs an immediate web service API call when the user opens the app for the first time to display the popular movies. The API call retrieves movie list which includes movie details such as title, language, poster path, backdrop path, vote count, vote average, genres, etc. 

Movie trailer addresses and reviews are retrieved using the same Async Task in background when Movie Detail activity is opened. 

Movie poster images,backdrop images and trailer thumbnails are retrieved using [Picasso](http://square.github.io/picasso/)

## Caching

The app saves movie details, genres, trailer address, reviews and favourites in the SQLite database on the device. The SQLLite database can be accessed by other applications by querying the Content Provider with appropriate Uri's.

## Playing Trailers

Trailers are launched using implicit intents and user can view the movie trailer using any application installed on their device which supports online video streaming.
