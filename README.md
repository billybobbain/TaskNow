## A program written by Claude 4.5 as prompted by me via chat. 

## Prompt 
>I want an android application that takes a list of tasks and builds a plan for further editing. It should use the following questions to gather the data. What task are you currently procrastinating on?
>Provide a brief description of the task.
>Why are you avoiding doing this task?
>What are the benefits of completing this task?
>Tasks can be broken down into subtasks. Name an easy subtask you can complete for this task
>How long will it take you to complete this subtask (in minutes)?
>Name a small reward for completing the subtask.

## Update 1
>I want this app to use persistent storage in sqlite.

## Update 2
>Can you change the package name to com.billybobbain.tasknow?

## Update 3
Can you upgrade devtools plugin to version 2.0.21-1.0.27?

## Update 4
>Can you upgrade devtools plugin in build.gradle.kts to 2.0.21-1.0.27?

## Update 5
>Can you add a summary of next 24 hours at the bottom of the screen? Include a diagram of what percentage of the day is consumed.

## Update 6
>very nice. can you make it easy to change the color scheme?

## Update 7
>Getting compile errors in TaskPlannerApp. The words "immutable" and "mutableStateOf" are red.

## Update 8
>I had to add those imports you just suggested to Theme.kt to get it to compile correctly. They weren't for MainActivity.kt. Or was Theme.kt embedded in MainActivity.kt. I am kinda new to Kotlin.

## Update 9
> How do I invoke the theme editor from the app?
The theme isn't editable from within the app at runtime - it's set at compile time in the code. To change the theme, you need to edit the color values in the Theme.kt file and rebuild the app.
If you want users to be able to change themes from within the app (like a settings screen where they can pick different color schemes), I can add that feature! It would involve:

A settings screen with theme options
Storing the theme preference in the database
Multiple pre-defined color schemes to choose from
The app dynamically switching themes based on user selection

Would you like me to add this functionality so users can change themes from a settings menu in the app?
## Update 10
>ah, I forgot. Yes, can you add a settings menu to change themes?


<img width="1075" height="2393" alt="Screenshot_20251029-185553" src="https://github.com/user-attachments/assets/20333244-a27d-4c4e-9388-e1ebc50bab91" /> 
