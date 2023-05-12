# Gymtracker


Gymtracker is a Javaapplication that allows users to share and track their workouts with eachother

Source code for the application can be found in the following folder: "Gymtracker\Gymtracker\src\main\java"

Here is the link for the SDK Windows: https://download2.gluonhq.com/openjfx/20.0.1/openjfx-20.0.1_windows-x64_bin-sdk.zip
and here is the link for Mac OS https://download2.gluonhq.com/openjfx/20.0.1/openjfx-20.0.1_osx-aarch64_bin-sdk.zip
If theres an issue with these SDKs for your operating system pick corresponding sdk for your OS https://gluonhq.com/products/javafx/

Once the SDK is downloaded you will have to add the SDK to your global library in Intellij, follow the instructions mentioned below

File -> Project Structure -> Global Libraries -> +(Press the + symbol on the left hand side)-> add all SDK files from the SDK Zip -> press OK
![image](https://user-images.githubusercontent.com/121502580/233309336-c07318c9-8ef5-4623-af0e-28ca93035c54.png)
Done

Now that the SDK is downloaded and setup you can run the class MainApplication found in "Gymtracker\Gymtracker\src\main\java\com\gymtracker\gymtracker"


Some of the functions in the program are not implemented yet. To use the program you will need to have set up the driver in the project ![image](https://github.com/timspel/Gymtracker/assets/121502580/de81b2fe-9b27-492a-aaff-53e872646517). If you do not see the JAR file like this you will need to go to File-> Project Structure -> Modules -> then press the + on the right hand side under dependencies -> Select the JAR file called "postgresql-42.5.1.jar" -> Press ok -> apply/ok -> Done
![image](https://github.com/timspel/Gymtracker/assets/121502580/be9ce591-9732-43a6-b39e-50d19217ada5)

Try not to input to much data into the database since we are still developing the program. We have identified a few problems with connections to the database where connection will get lost when running too many SQL queries. For further help contact Timid#0584 on discord: 

 

