// ===== TaskTemplate.kt =====
package com.billybobbain.tasknow

/**
 * Represents a task template with pre-filled data
 */
data class TaskTemplate(
    val id: String,
    val name: String,
    val category: TaskTemplateCategory,
    val taskName: String,
    val description: String,
    val avoidanceReason: String,
    val benefits: String,
    val subtasks: List<SubtaskTemplateData>,
    val isRepeating: Boolean = false
)

/**
 * Template data for a single subtask
 */
data class SubtaskTemplateData(
    val description: String,
    val timeEstimate: String,
    val reward: String
)

/**
 * Categories for organizing templates
 */
enum class TaskTemplateCategory(val displayName: String, val emoji: String) {
    HEALTH_FITNESS("Health & Fitness", "🏋️"),
    HOME_CLEANING("Home & Cleaning", "🏠"),
    WORK_PRODUCTIVITY("Work & Productivity", "💼"),
    PERSONAL_CARE("Personal Care", "🌟"),
    LEARNING("Learning", "📚"),
    SPORTS_BETTING("Sports Research & DFS", "🏈")
}

/**
 * Built-in task templates
 * Designed to be easily extendable with template packs in the future
 */
object TaskTemplates {

    val allTemplates = listOf(
        // Health & Fitness Templates
        TaskTemplate(
            id = "gym_workout",
            name = "Gym Workout",
            category = TaskTemplateCategory.HEALTH_FITNESS,
            taskName = "Complete gym workout",
            description = "Full workout session at the gym including cardio and strength training",
            avoidanceReason = "Feeling tired, don't want to leave the house, intimidated by the gym",
            benefits = "Improved health, more energy, better mood, stronger body, stress relief",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Get dressed in workout clothes", "5", "Feel ready and motivated"),
                SubtaskTemplateData("Drink water and grab a snack", "5", "Pre-workout energy boost"),
                SubtaskTemplateData("Drive/walk to the gym", "15", "Arrived at the gym"),
                SubtaskTemplateData("Warm up with 10 min cardio", "10", "Body is warmed up"),
                SubtaskTemplateData("Complete strength training routine", "30", "Muscles worked hard"),
                SubtaskTemplateData("Cool down with stretching", "10", "Feeling accomplished and energized")
            )
        ),

        TaskTemplate(
            id = "morning_run",
            name = "Morning Run",
            category = TaskTemplateCategory.HEALTH_FITNESS,
            taskName = "Go for a morning run",
            description = "Start the day with an energizing run around the neighborhood",
            avoidanceReason = "Bed is too comfortable, it's cold outside, feeling lazy",
            benefits = "Great start to the day, mental clarity, cardiovascular health, endorphin rush",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Put on running gear", "5", "Ready to go"),
                SubtaskTemplateData("Do quick stretches", "5", "Muscles loosened up"),
                SubtaskTemplateData("Run for 20-30 minutes", "25", "Runner's high achieved"),
                SubtaskTemplateData("Cool down walk", "5", "Caught my breath"),
                SubtaskTemplateData("Hydrate and shower", "15", "Feeling fresh and energized")
            )
        ),

        TaskTemplate(
            id = "yoga_session",
            name = "Yoga Session",
            category = TaskTemplateCategory.HEALTH_FITNESS,
            taskName = "Complete yoga practice",
            description = "Mindful yoga session for flexibility, strength, and relaxation",
            avoidanceReason = "Too busy, feeling stiff, worried I'm not flexible enough",
            benefits = "Reduced stress, improved flexibility, better posture, mental calm",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Set up yoga mat and space", "3", "Peaceful space created"),
                SubtaskTemplateData("Start with breathing exercises", "5", "Centered and focused"),
                SubtaskTemplateData("Do sun salutations", "10", "Body is warming up"),
                SubtaskTemplateData("Practice main poses", "20", "Deep stretches completed"),
                SubtaskTemplateData("End with savasana", "7", "Completely relaxed")
            )
        ),

        // Home & Cleaning Templates
        TaskTemplate(
            id = "clean_kitchen",
            name = "Clean Kitchen",
            category = TaskTemplateCategory.HOME_CLEANING,
            taskName = "Deep clean the kitchen",
            description = "Thoroughly clean and organize the kitchen space",
            avoidanceReason = "Overwhelming mess, don't know where to start, would rather relax",
            benefits = "Clean cooking space, reduced stress, healthier environment, sense of accomplishment",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Clear and wipe all counters", "10", "Clear workspace"),
                SubtaskTemplateData("Wash all dishes", "15", "Clean dishes stacked"),
                SubtaskTemplateData("Clean the stove and microwave", "10", "Appliances sparkling"),
                SubtaskTemplateData("Wipe down cabinets and fridge", "10", "Surfaces clean"),
                SubtaskTemplateData("Sweep and mop the floor", "15", "Floor is spotless"),
                SubtaskTemplateData("Take out trash and recycling", "5", "Kitchen smells fresh")
            )
        ),

        TaskTemplate(
            id = "do_laundry",
            name = "Do Laundry",
            category = TaskTemplateCategory.HOME_CLEANING,
            taskName = "Complete laundry cycle",
            description = "Wash, dry, fold, and put away all laundry",
            avoidanceReason = "Boring task, takes a long time, easy to forget about",
            benefits = "Clean clothes available, organized closet, one less thing to worry about",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Sort clothes into loads", "5", "Organized by color/type"),
                SubtaskTemplateData("Start first wash load", "3", "Washer running"),
                SubtaskTemplateData("Transfer to dryer when done", "5", "Dryer running"),
                SubtaskTemplateData("Fold clean clothes", "15", "Neat stacks of clothes"),
                SubtaskTemplateData("Put away in closet/drawers", "10", "Everything put away")
            )
        ),

        TaskTemplate(
            id = "tidy_bedroom",
            name = "Weekly Room Reset",
            category = TaskTemplateCategory.HOME_CLEANING,
            taskName = "Reset and organize bedroom",
            description = "Weekly bedroom cleaning and organization routine",
            avoidanceReason = "Room is too messy to know where to start, takes too long",
            benefits = "Better sleep environment, reduced anxiety, easier to find things",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Make the bed", "3", "Bed looks inviting"),
                SubtaskTemplateData("Pick up items off the floor", "10", "Clear floor space"),
                SubtaskTemplateData("Organize desk and nightstand", "10", "Surfaces are clear"),
                SubtaskTemplateData("Vacuum or sweep the floor", "10", "Clean floor"),
                SubtaskTemplateData("Take out any trash", "3", "Room feels fresh")
            )
        ),

        // Work & Productivity Templates
        TaskTemplate(
            id = "email_inbox",
            name = "Process Email Inbox",
            category = TaskTemplateCategory.WORK_PRODUCTIVITY,
            taskName = "Get to inbox zero",
            description = "Process and organize all emails in inbox",
            avoidanceReason = "Inbox is overwhelming, some emails need difficult responses, takes mental energy",
            benefits = "Clear mind, no missed important messages, feel in control, reduced anxiety",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Delete obvious spam/junk", "5", "Less clutter"),
                SubtaskTemplateData("Unsubscribe from unwanted lists", "5", "Fewer future emails"),
                SubtaskTemplateData("Quick replies to easy emails", "15", "Quick wins done"),
                SubtaskTemplateData("Flag important emails for later", "5", "Priorities identified"),
                SubtaskTemplateData("Draft responses to complex emails", "20", "Hard part started"),
                SubtaskTemplateData("Archive/file processed emails", "5", "Inbox at zero!")
            )
        ),

        TaskTemplate(
            id = "meeting_prep",
            name = "Prepare for Important Meeting",
            category = TaskTemplateCategory.WORK_PRODUCTIVITY,
            taskName = "Prepare for upcoming meeting",
            description = "Thoroughly prepare for an important meeting or presentation",
            avoidanceReason = "Nervous about the meeting, don't know what to prepare, procrastinating on hard thinking",
            benefits = "Confidence in the meeting, better outcomes, reduced anxiety, look professional",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Review meeting agenda and goals", "10", "Know what to expect"),
                SubtaskTemplateData("Research attendees and their interests", "15", "Understand the audience"),
                SubtaskTemplateData("Prepare talking points or slides", "30", "Core content ready"),
                SubtaskTemplateData("Anticipate questions and prepare answers", "15", "Ready for Q&A"),
                SubtaskTemplateData("Do a quick practice run", "10", "Feeling confident"),
                SubtaskTemplateData("Gather any materials needed", "5", "Everything ready to go")
            )
        ),

        TaskTemplate(
            id = "new_project",
            name = "Start New Project",
            category = TaskTemplateCategory.WORK_PRODUCTIVITY,
            taskName = "Kick off new project",
            description = "Get a new project organized and started with momentum",
            avoidanceReason = "Project feels too big, don't know where to start, perfectionism paralysis",
            benefits = "Project momentum started, clear path forward, reduced overwhelm, progress made",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Define project goals and scope", "15", "Clear direction"),
                SubtaskTemplateData("Break down into major milestones", "15", "Roadmap created"),
                SubtaskTemplateData("Identify first 3 concrete tasks", "10", "Know next steps"),
                SubtaskTemplateData("Set up project folder/workspace", "10", "Organized workspace"),
                SubtaskTemplateData("Complete first small task", "20", "Momentum started!"),
                SubtaskTemplateData("Schedule time for next tasks", "5", "Committed to continue")
            )
        ),

        // Personal Care Templates
        TaskTemplate(
            id = "morning_routine",
            name = "Morning Routine",
            category = TaskTemplateCategory.PERSONAL_CARE,
            taskName = "Complete morning routine",
            description = "Energizing morning routine to start the day right",
            avoidanceReason = "Want to stay in bed, feeling groggy, rushing to get ready",
            benefits = "Better mood all day, more energy, productive day ahead, self-care completed",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Drink a full glass of water", "2", "Hydrated and alert"),
                SubtaskTemplateData("Shower and personal hygiene", "15", "Feeling fresh"),
                SubtaskTemplateData("Eat a healthy breakfast", "15", "Energized for the day"),
                SubtaskTemplateData("Review daily goals/to-do list", "5", "Know what's ahead"),
                SubtaskTemplateData("5 minutes of meditation or breathing", "5", "Centered and calm")
            )
        ),

        TaskTemplate(
            id = "evening_routine",
            name = "Evening Wind-Down",
            category = TaskTemplateCategory.PERSONAL_CARE,
            taskName = "Evening wind-down routine",
            description = "Relaxing evening routine for better sleep and recovery",
            avoidanceReason = "Too tired to do anything, distracted by screens, want to stay up late",
            benefits = "Better sleep quality, wake up refreshed, reduced stress, healthy habit",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Tidy up living space", "10", "Peaceful environment"),
                SubtaskTemplateData("Prepare for tomorrow (clothes, lunch, etc.)", "10", "Tomorrow is easier"),
                SubtaskTemplateData("Personal hygiene routine", "10", "Feeling clean"),
                SubtaskTemplateData("Read or journal for 15 minutes", "15", "Mind is calm"),
                SubtaskTemplateData("Set phone aside, dim lights", "5", "Ready for restful sleep")
            )
        ),

        // Learning Templates
        TaskTemplate(
            id = "study_session",
            name = "Focused Study Session",
            category = TaskTemplateCategory.LEARNING,
            taskName = "Complete study session",
            description = "Productive study session with breaks and active learning",
            avoidanceReason = "Material is hard, easy to get distracted, don't feel motivated",
            benefits = "Better understanding, improved grades, confidence in the material, progress made",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Set up study space and materials", "5", "Ready to focus"),
                SubtaskTemplateData("Review previous notes/material", "10", "Refreshed on basics"),
                SubtaskTemplateData("Study new material for 25 min (Pomodoro)", "25", "First session complete"),
                SubtaskTemplateData("Take 5 minute break", "5", "Brain refreshed"),
                SubtaskTemplateData("Practice problems or active recall", "25", "Applied knowledge"),
                SubtaskTemplateData("Summarize key takeaways", "10", "Cemented understanding")
            )
        ),

        // Sports Research & DFS Templates
        TaskTemplate(
            id = "daily_dfs_research",
            name = "Daily DFS Lineup Research",
            category = TaskTemplateCategory.SPORTS_BETTING,
            taskName = "Research and build DFS lineup",
            description = "Comprehensive research for daily fantasy sports lineup on DraftKings/Underdog",
            avoidanceReason = "Overwhelming amount of data, easy to make quick emotional picks, analysis paralysis",
            benefits = "Data-driven decisions, better ROI, avoid tilt picks, increased win rate",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Check injury reports and news", "10", "Know who's playing"),
                SubtaskTemplateData("Review Vegas lines and totals", "10", "Identified high-scoring games"),
                SubtaskTemplateData("Analyze recent player performance trends", "15", "Hot and cold players identified"),
                SubtaskTemplateData("Check opponent defensive rankings", "10", "Found favorable matchups"),
                SubtaskTemplateData("Review weather for outdoor games", "5", "Weather factors noted"),
                SubtaskTemplateData("Build optimal lineup within salary cap", "15", "Lineup constructed"),
                SubtaskTemplateData("Double-check lineup and submit", "5", "Confident in picks")
            )
        ),

        TaskTemplate(
            id = "prop_bet_research",
            name = "Player Prop Research",
            category = TaskTemplateCategory.SPORTS_BETTING,
            taskName = "Research player props and find value",
            description = "Analyze individual player prop bets and identify profitable opportunities",
            avoidanceReason = "Too many props to choose from, hard to find edge, tempting to chase losses",
            benefits = "Find +EV bets, avoid bad lines, disciplined betting approach, better bankroll management",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Scan available props across books", "10", "Props cataloged"),
                SubtaskTemplateData("Identify 3-5 props of interest", "5", "Narrowed focus"),
                SubtaskTemplateData("Check player recent stat trends", "15", "Trends identified"),
                SubtaskTemplateData("Review matchup history and defense", "10", "Matchup edge found"),
                SubtaskTemplateData("Compare lines across sportsbooks", "10", "Best odds identified"),
                SubtaskTemplateData("Calculate unit sizes based on confidence", "5", "Bankroll allocated"),
                SubtaskTemplateData("Place bets and track in spreadsheet", "5", "Bets logged")
            )
        ),

        TaskTemplate(
            id = "weekly_slate_analysis",
            name = "Weekly Slate Analysis",
            category = TaskTemplateCategory.SPORTS_BETTING,
            taskName = "Analyze upcoming week's games",
            description = "Review the full slate of games for the week and identify betting opportunities",
            avoidanceReason = "Week seems far away, too much data to process, easier to wait until gameday",
            benefits = "Early line value, better prep time, identify key matchups, avoid rushed decisions",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Review full week's schedule", "10", "Know all matchups"),
                SubtaskTemplateData("Identify 5-7 games of interest", "10", "Focus narrowed"),
                SubtaskTemplateData("Check opening lines and movement", "10", "Line value spotted"),
                SubtaskTemplateData("Review team injury reports", "15", "Health status known"),
                SubtaskTemplateData("Read expert analysis and picks", "15", "Multiple perspectives gathered"),
                SubtaskTemplateData("Note key trends and angles", "10", "Edge identified"),
                SubtaskTemplateData("Create shortlist for live bets", "5", "Week planned out")
            )
        ),

        TaskTemplate(
            id = "bankroll_review",
            name = "Weekly Bankroll Review",
            category = TaskTemplateCategory.SPORTS_BETTING,
            taskName = "Review betting performance and adjust strategy",
            description = "Analyze week's betting results and make strategic adjustments",
            avoidanceReason = "Don't want to face losses, hard to be honest about mistakes, takes discipline",
            benefits = "Improved long-term results, identify leaks, stay within budget, reduce tilt",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Calculate week's profit/loss", "5", "Know the numbers"),
                SubtaskTemplateData("Review winning bets - what worked", "10", "Patterns identified"),
                SubtaskTemplateData("Analyze losing bets - what failed", "10", "Mistakes recognized"),
                SubtaskTemplateData("Calculate ROI and win rate", "10", "Performance measured"),
                SubtaskTemplateData("Adjust unit sizes if needed", "5", "Bankroll protected"),
                SubtaskTemplateData("Set limits for next week", "5", "Guardrails in place"),
                SubtaskTemplateData("Document lessons learned", "5", "Growth tracked")
            )
        ),

        TaskTemplate(
            id = "game_deep_dive",
            name = "Single Game Deep Dive",
            category = TaskTemplateCategory.SPORTS_BETTING,
            taskName = "Deep analysis of one key game",
            description = "Thorough research on a single high-value game or matchup",
            avoidanceReason = "Seems like overkill for one game, tempting to rely on gut feel, time-consuming",
            benefits = "High-confidence play, edge over casual bettors, informed same-game parlay, reduced variance",
            isRepeating = true,
            subtasks = listOf(
                SubtaskTemplateData("Study both teams' recent form", "15", "Team trends known"),
                SubtaskTemplateData("Analyze head-to-head history", "10", "Matchup dynamics understood"),
                SubtaskTemplateData("Review key player matchups", "15", "Individual battles identified"),
                SubtaskTemplateData("Check coaching tendencies", "10", "Strategy insights gained"),
                SubtaskTemplateData("Factor in situational spots", "10", "Context considered"),
                SubtaskTemplateData("Build game script hypothesis", "10", "Flow predicted"),
                SubtaskTemplateData("Identify best bet types for game", "10", "Plays selected")
            )
        )
    )

    /**
     * Get templates by category
     */
    fun getTemplatesByCategory(category: TaskTemplateCategory): List<TaskTemplate> {
        return allTemplates.filter { it.category == category }
    }

    /**
     * Get all categories that have templates
     */
    fun getAvailableCategories(): List<TaskTemplateCategory> {
        return allTemplates.map { it.category }.distinct().sortedBy { it.displayName }
    }
}
