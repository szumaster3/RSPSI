[![License][license-shield]][license-url]

# RSPSI

Follow the steps below to set up and run the editor properly.

### Getting Started

1. **Fork or Clone the Repository**  
   Either fork this project or clone it directly to your local machine.

2. **Select Java 11**  
   Ensure your IDE (e.g., IntelliJ IDEA) is configured to use Java 11.

3. **Build the Client JAR**
   - In IntelliJ IDEA, go to the right-hand panel and select the `client` module.
   - Click **Build → Build JAR** to generate the client JAR.

4. **Build the Rest of the Project and Plugins**
   - Select the **Plugins** module in the right-hand panel.
   - Run **Source Generation** and execute `serviceLoaderBuild`.

5. **Build and Move Plugin**
   - After source generation, expand the plugin module.
   - Execute `jar` followed by `buildAndMove`. This will create the plugin JAR in `Editor/plugins/inactive/`.

6. **Run the Editor**
   - Go to `Editor → Application → Run` in your IDE, select the plugin, and launch the editor.

7. **Editor Settings**
   - Editor settings are stored in the `.rspsi/` directory, located alongside your `.idea` folder.

[license-shield]: https://img.shields.io/badge/license-MIT-violet
[license-url]: https://opensource.org/license/mit