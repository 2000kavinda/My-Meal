# My Meal


<img src="https://github.com/user-attachments/assets/8325bfd3-0bd3-49da-aa73-5d94273e0dbf" alt="My Meal" width="300" height="300">

## Problem Scenario

In today's world, many individuals face health challenges due to unhealthy meal patterns. Most meal-ordering platforms prioritize sales and user preferences while overlooking the impact of meals on users' health.

## Solution Application

The **My Meal App** is designed to bridge this gap by providing a platform that not only facilitates meal ordering but also promotes healthier eating habits. The app considers users’ health status, tracks their nutrition levels, and offers personalized AI-based suggestions for improved well-being.

### Key Features

1. **Health-Centric Meal Recommendations**
   - The app uses a macronutrient-based algorithmic logic to assess users' health status and recommend meals accordingly.
   - All user meal orders are stored in a database, and this data is utilized by the algorithm to provide accurate health status and suggestions.

2. **User Metadata Integration**
   - During registration, users provide essential metadata such as gender, activity level, and goals.
   - This information is incorporated into the algorithm to enhance the precision of health status calculations.

3. **Meal Scanning for External Meals**
   - Users may consume meals outside the app. The meal scanning feature allows users to scan their meals and receive a detailed nutritional report.
   - The scanned meal nutrition data is stored in the database and used in health status calculations, improving accuracy even for meals not ordered through the app.

4. **Play the Game and Earn Coins**
   - To boost user engagement, the app includes a game where users can play and earn coins.
   - These coins can be used to pay for meals.
   - Users can only play the game if their health status remains within a healthy range. If the health status is unhealthy, access to the game is restricted.
   - To maintain business balance, the coin collection rate and coin economic value are strategically adjusted by the game physics.

### Focus Areas

The **My Meal App** prioritizes:
- **User Health**: By promoting healthier meal choices and tracking nutritional levels.
- **User Experience**: With a seamless and intuitive platform & rsponsive user interfaces.
- **User Engagement**: Through gamification and reward systems to keep users motivated and involved.
- **Business Sustainability**: By strategically adjusting the coin collection rate and coin economic value to maintain financial balance.

<br>

## **Technologies and Frameworks Used**

### **Development Platform**
- **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP):**  
  Utilized as the primary platforms for cross-platform application development.

### **Backend and Database**
- **Firebase Firestore:**  
  Employed to store and manage application data efficiently.
- **Firebase Storage:**  
  Used for storing media-related assets such as images.

### **Game Development**
- Used for developing the "Play & Earn Coins" game, providing an engaging gamification experience for users.

### **Artificial Intelligence**
- **OpenAI Integration:**  
  - Used to analyze user health status.  
  - Provides personalized recommendations for meal choices and actionable health tips.

---

## **Data Sources**

### **Meal Image Processing**
- **LogMeal API**  
  - **Endpoint 1:** (https://api.logmeal.com/v2/image/segmentation/complete)  
    Used to upload meal images and generate segmentation data for nutrition analysis.  
  - **Endpoint 2:** (https://api.logmeal.com/v2/recipe/nutritionalInfo)  
    Processes the meal image to generate detailed nutritional reports.

### **Nutritional Information Retrieval**
- **CalorieNinjas API**  
  - **Endpoint:** (https://api.calorieninjas.com/v1/nutrition)  
    Provides nutritional data by receiving the meal name as input, enabling accurate tracking and analysis.


<br>

# Running Application on Android Emulator and Desktop (Windows) platforms Demo 
All user interfaces are full responsive (Showed in the screenshots)

### **User Authentication**
- **Log In**  
  Secure access to the application using user credentials.
  ![Group 758530619](https://github.com/user-attachments/assets/6e512f52-b4e5-4c27-98ac-565481f4a87f)

- **Register**  
  Collect user metadata during registration to enhance the accuracy of health status calculations.
![Group 758530620](https://github.com/user-attachments/assets/d6ae9289-744c-4416-bffe-fe396111887c)

---

### **Nutrition Management**
- **Insert External Nutrition**  
  - Users can input the nutritional details of meals consumed outside the app by scanning the meal image.  
  - The system generates a nutrition report, saves it to the database, and integrates it into health status calculations.
![Group 758530621](https://github.com/user-attachments/assets/b7af137f-db20-44aa-bd82-ef94f46e269e)
![Group 758530622](https://github.com/user-attachments/assets/ed4d8fe1-f93b-46ea-b37b-0cf304b73a2e)

---

### **Meal Selection and Purchase**
- **Choose and Buy Meal**  
  - Users can browse and order meals directly through the app.
      ![Group 758530623](https://github.com/user-attachments/assets/d9c9ef4d-c122-4d6f-a5eb-cea7fa21637f)

  - The application analyzes the selected meal's nutritional value and determines whether it is **healthy** or **unhealthy** for the user based on their health status and nutrition levels.  
  - **AI-based suggestions** are provided to guide users on:  
    - What to eat.  
    - Recommended actions for better health.  
![Group 758530624](https://github.com/user-attachments/assets/325e60d8-05f6-4719-9031-3b38e9a8aa2d)

- **Payment Options**  
  - Users can pay for meals using:  
    1. **Earned Coins**: Collected by playing in-app games.  
    2. **Direct Payment**: Using a credit/debit card.  
  - **Test Mode Card Details** (for testing purposes):  
    - **Card Number**: `4242424242424242`  
    - **CVV**: `123`  
    - **Expiry**: `09/29`  
    - **Current Balance**: `$100,000`
![Group 758530625](https://github.com/user-attachments/assets/882b6597-8393-4c29-82d9-8b483becd7d8)

---

### **Gamification**
- **Play Games and Earn Coins**  
  - Users in **good health** can play games and earn coins as rewards.  
  - If the user's health status is **unhealthy**, access to this feature is restricted until they return to a healthy status.
![Group 758530627](https://github.com/user-attachments/assets/d61e598a-e12c-47c7-8a63-93e2ec5f1cb1)
![Group 758530628](https://github.com/user-attachments/assets/e6434d39-7994-4728-8559-db751c1ab827)

---

### **Order History and Reordering**
- **Reorder Previous Meals**  
  - A history feature allows users to reorder previously purchased items for convenience and time-saving.
![Group 758530629](https://github.com/user-attachments/assets/e8bad4b6-8677-4a26-b074-55a763149ae1)

---

### **User Profile**
- **Profile Features**  
  - View personal details.  
  - Check earned coin balance.  
  - Access a summary of their overall health status.  
  - Health status is calculated using the **Macronutrient-Based Algorithm**.

![Group 758530630](https://github.com/user-attachments/assets/7dcb1dfa-f4ca-41ee-808c-113e69541aeb)

![Group 758530631](https://github.com/user-attachments/assets/1bcada2c-89a1-49c3-9dad-4ec35754849e)


<br>

# Application Architecture

The application's architecture goes with Model View Controller (MVC) Architecture.

![My meal](https://github.com/user-attachments/assets/cb0c55f6-f110-4af5-ab68-28a5b86eb6fd)

<br>

# Used Libraries

| **Library**                                          | **Usage**                                                                                                                                                              |
|------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `libs.gitlive.firebase.firestore`                   | Firebase Firestore library for Kotlin Multiplatform. Used to interact with Firestore in a platform-agnostic way.                                                     |
| `libs.kotlinx.coroutines.swing`                    | Provides coroutine support for desktop UI frameworks like Swing, enabling asynchronous programming in desktop applications.                                          |
| `libs.bundles.ktor`                                 | A bundle of Ktor modules (likely includes HTTP client and server utilities). Simplifies HTTP networking across multiplatform projects.                              |
| `io.ktor:ktor-client-core:2.3.4`                   | Core library for the Ktor HTTP client. Provides the foundation for making HTTP requests in a multiplatform environment.                                              |
| `io.ktor:ktor-client-cio:2.3.4`                    | CIO (Coroutine-based I/O) engine for Ktor HTTP client. Optimized for asynchronous HTTP networking across platforms.                                                  |
| `libs.coil.compose`                                | Coil image-loading library with integration for Jetpack Compose. Allows efficient and easy image handling in Compose UIs.                                           |
| `libs.coil.network.ktor`                           | Coil library for loading images using Ktor's networking capabilities. Enables seamless network-based image loading in KMP projects.                                 |
| `libs.ktor.client.core`                            | Additional core components for the Ktor HTTP client. Extends networking capabilities.                                                                                |
| `org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07` | Navigation library for Jetpack Compose, specific to Android. Provides tools for managing screen transitions and navigation in Compose apps.                        |
| `io.coil-kt.coil3:coil-compose:3.0.0-alpha06`      | The latest version of Coil for Compose. Adds advanced image-loading features and better Compose integration for efficient UI rendering with images.                  |


<br>

## Steps to Run the Application

### 1. Clone or Download the Project
- Clone or download the project from this GitHub repository.

### 2. Open the Project
- Open the project in **Android Studio**.

### 3. Set Up API Keys
1. Access the required API keys from the following link:  
   (https://drive.google.com/file/d/11hVkmHqGL61MBP_twZFH9iD7sbRG1pcR/view?usp=sharing)
2. The file contains 3 keys. Copy all three lines and follow these steps:
   - Navigate to the file:  
     `composeApp/src/commonMain/kotlin/org/myapp/mymeal/utils/Constants.kt`
   - Paste the keys and replace the **first three constant values** in the `Constants.kt` file with the new values from the text file.

### 4. Run the Application
![image](https://github.com/user-attachments/assets/247afb6a-05bc-4036-9593-be5661568431)

#### For Android App:
1. In **Android Studio**, select **`MainActivity`** as the target activity.
2. Click the **Run** button to launch the Android application.

#### For Desktop App:
1. In **Android Studio**, select the run configuration: **`MyMeal[composeApp:run]`**.
2. Click the **Run** button to launch the desktop application.


---


## Thank You!
Thank you for exploring the **My Meal Application**. If you have any questions or feedback, feel free to reach out.

**Developer:** Kavinda Udara  
**Email:** kavindaudara75@gmail.com 
