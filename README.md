###Automobile ECU Encryption###
This project provides utilities for  user login and registration, AES encryption and decryption of ECU data, brute-force key testing, random key generation and analyzing speed data from CSV files. The project is implemented in Java and utilizes several techniques including AES CBC mode with PKCS5 padding, brute-force key testing, and graphical visualization of speed data.

###Features###
1. User Authentication and Registration
DataBaseConnection.java: Establishes a connection to a MySQL database, where user data is stored in the ECU database.

Login.java: Provides a login interface where users can log in with their user ID and password. The application verifies the credentials and redirects the user to the appropriate dashboard (Admin or User).

Register.java: Provides a registration interface that allows new users to create an account by entering their username, user ID, role (admin/user), and password. The user information is then saved in the database.

2.Admin and User Pages
##Admin Page Features##
The Admin Page serves as the main interface for administrators to manage encryption requests, generate reports, and handle data analysis. Below are the key features of the admin interface:

a. Request Management
Submit Requests: Admins can view the list of user-submitted encryption requests.

Accept/Reject Requests: Admins can review and either accept or reject encryption requests based on user submission.

Encrypt and Decrypt Files: Once requests are accepted, admins can initiate file encryption and decryption tasks for the users.

Generate PDF Reports: Admins can generate PDF reports for each accepted encryption request, detailing the user ID, encrypted and decrypted file information, timestamps, and other relevant metadata.

b. User Interaction
Admins can manage user accounts, view their details, and manage roles (admin/user).

Admins can monitor the status of each request and ensure timely processing.

c. Generate Reports
Admins have the ability to generate PDF reports for accepted encryption requests using the iText library.

##User Page Features##
The User Page provides an interface for regular users to submit encryption requests, view the status of their requests, and analyze encrypted data. Below are the key features of the user interface:

a. Submit Encryption Requests
Users can submit their requests to encrypt a file. This includes providing the file for encryption and the necessary details for the process.

b. View Request Status
Users can view the current status of their encryption request (pending, accepted, rejected, or processed).

Users are notified when their request has been processed and can download the decrypted file if required.

c. Data Analysis
Analyze Speed Data: Users can upload a CSV file containing speed data. The system extracts the "Speed" column from the file and visualizes the data in graphical form, aiding in the analysis of speed trends over time.


3. AES Encryption/Decryption
FileEncryptionUtil.java: This class handles the encryption and decryption of files using AES in CBC mode with PKCS5 padding. It generates a random Initialization Vector (IV) for encryption and extracts it during decryption to ensure the security of the process.

4. Brute Force Decryption
AESBruteForce.java: This class performs brute-force decryption of AES-encrypted files. It tests all possible 4-digit key combinations from 0000 to 9999 to identify the correct encryption key.

AESBruteForceDecryptor.java: This class extends brute-force decryption, allowing key testing with larger keyspaces (characters between a-z and 0-9 for 4 to 6 character keys).

5. Speed Data Analysis
AnalyzeAction.java: This class allows the user to upload a CSV file containing speed data, extracts the "Speed" column, and displays the data graphically using the JFreeChart library for better visualization and analysis.

6. Random Key Generation
Test.java: This class generates random AES keys and sequentially tries them for decryption. If a decryption attempt fails, a new random key is generated and tried again.

7. Brute Force Attack
AESBruteForceDecryptor.java: Implements a recursive brute-force attack that uses a charset of characters and tests keys of lengths between 4 and 6 characters. This approach systematically tests potential keys until the correct one is found.

Requirements
Java 8 or higher

MySQL Database with a user table that includes userid, username, role, and password columns.

JFreeChart library (for graph visualization of speed data).
link:http://www.jfree.org/jfreechart/
