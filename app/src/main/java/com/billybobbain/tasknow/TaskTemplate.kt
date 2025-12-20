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
    MEDICAL("Medical Procedures", "🏥"),
    HOME_CLEANING("Home & Cleaning", "🏠"),
    WORK_PRODUCTIVITY("Work & Productivity", "💼"),
    PERSONAL_CARE("Personal Care", "🌟"),
    LEARNING("Learning", "📚"),
    SPORTS_BETTING("Sports Research & DFS", "🏈"),
    APP_DEVELOPER("App Development", "📱"),
    CHRISTMAS("Christmas", "🎄")
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

        // Medical Procedures Templates
        TaskTemplate(
            id = "colonoscopy",
            name = "Get a Colonoscopy",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Get a colonoscopy",
            description = "Complete colonoscopy procedure for colon cancer screening",
            avoidanceReason = "Fear of discomfort, anxiety about the prep process, putting off medical procedures, embarrassment",
            benefits = "Peace of mind, early detection of colon cancer, preventive health maintenance, crossing off a major health milestone, potentially life-saving",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Schedule the appointment", "15", "Date set, commitment made"),
                SubtaskTemplateData("Arrange transportation", "15", "Someone confirmed to drive you"),
                SubtaskTemplateData("Get prep kit and instructions from pharmacy", "20", "Supplies in hand"),
                SubtaskTemplateData("Buy prep day supplies (clear liquids, etc.)", "30", "Ready for prep day"),
                SubtaskTemplateData("Clear your schedule for prep and procedure day", "10", "Time blocked off"),
                SubtaskTemplateData("Do the bowel prep", "240", "Worst part done, almost there!"),
                SubtaskTemplateData("Attend the procedure", "120", "Done! Peace of mind achieved")
            )
        ),

        TaskTemplate(
            id = "annual_physical",
            name = "Annual Physical Exam",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Complete annual physical exam",
            description = "Annual checkup with primary care doctor for preventive health",
            avoidanceReason = "Feeling fine so seems unnecessary, worried they'll find something wrong, takes time out of schedule",
            benefits = "Catch issues early, establish health baseline, update prescriptions if needed, peace of mind, responsible self-care",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Call to schedule appointment", "10", "Appointment on calendar"),
                SubtaskTemplateData("Fast if bloodwork required", "480", "Ready for accurate results"),
                SubtaskTemplateData("Gather insurance card and ID", "5", "Paperwork ready"),
                SubtaskTemplateData("List current medications and questions", "10", "Prepared for visit"),
                SubtaskTemplateData("Attend the appointment", "60", "Checkup complete!"),
                SubtaskTemplateData("Follow up on any referrals or prescriptions", "15", "All tasks handled")
            )
        ),

        TaskTemplate(
            id = "dental_cleaning",
            name = "Dental Cleaning",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Get dental cleaning and checkup",
            description = "Six-month dental cleaning and oral health examination",
            avoidanceReason = "Anxiety about the dentist, teeth are sensitive, costs money, afraid of cavities being found",
            benefits = "Prevent cavities and gum disease, fresh clean feeling, catch problems early, maintain dental health, avoid bigger issues later",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Call dentist to schedule appointment", "10", "Appointment booked"),
                SubtaskTemplateData("Check insurance coverage", "5", "Know what's covered"),
                SubtaskTemplateData("Brush and floss well before appointment", "5", "Ready for cleaning"),
                SubtaskTemplateData("Attend cleaning appointment", "60", "Teeth cleaned and checked"),
                SubtaskTemplateData("Schedule next cleaning in 6 months", "5", "Future appointment set"),
                SubtaskTemplateData("Follow any dentist recommendations", "15", "Dental health maintained")
            )
        ),

        TaskTemplate(
            id = "eye_exam",
            name = "Eye Exam",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Get comprehensive eye exam",
            description = "Annual eye exam to check vision and eye health",
            avoidanceReason = "Vision seems fine, worried about needing glasses, costs money, takes time",
            benefits = "Detect vision changes early, check for eye diseases, update prescription if needed, prevent eye strain headaches",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Find optometrist and schedule appointment", "15", "Appointment scheduled"),
                SubtaskTemplateData("Check vision insurance benefits", "5", "Costs understood"),
                SubtaskTemplateData("Bring current glasses if applicable", "2", "Ready for comparison"),
                SubtaskTemplateData("Attend eye exam", "45", "Exam complete"),
                SubtaskTemplateData("Order new glasses or contacts if needed", "20", "New eyewear on the way"),
                SubtaskTemplateData("Schedule next annual exam", "5", "Future care planned")
            )
        ),

        TaskTemplate(
            id = "blood_work",
            name = "Get Blood Work Done",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Complete lab work and blood tests",
            description = "Get prescribed blood work and lab tests completed",
            avoidanceReason = "Needle anxiety, have to fast beforehand, results might show something wrong, haven't made time",
            benefits = "Important health information, catch issues early, doctor can adjust treatment, complete medical checkup",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Get lab order from doctor", "10", "Order in hand"),
                SubtaskTemplateData("Find nearest lab location and hours", "10", "Location identified"),
                SubtaskTemplateData("Fast overnight if required", "720", "Ready for accurate results"),
                SubtaskTemplateData("Bring insurance card and ID to lab", "5", "Paperwork ready"),
                SubtaskTemplateData("Complete blood draw at lab", "20", "Hardest part done!"),
                SubtaskTemplateData("Follow up on results with doctor", "15", "Results reviewed and understood")
            )
        ),

        TaskTemplate(
            id = "pap_smear",
            name = "Pap Smear/Pelvic Exam",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Get pap smear and pelvic exam",
            description = "Routine cervical cancer screening and gynecological checkup",
            avoidanceReason = "Embarrassment, feeling vulnerable, discomfort with the speculum, anxiety about the process",
            benefits = "Early detection of cervical cancer, reproductive health check, peace of mind, preventive care",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Schedule appointment with gynecologist", "10", "Appointment booked"),
                SubtaskTemplateData("Avoid intercourse/douching 48h before", "5", "Properly prepared"),
                SubtaskTemplateData("Write down any symptoms or questions", "10", "Ready to discuss concerns"),
                SubtaskTemplateData("Attend appointment (usually 10-20 min)", "45", "Exam complete!"),
                SubtaskTemplateData("Wait for results (typically 1-2 weeks)", "5", "Results pending"),
                SubtaskTemplateData("Schedule next screening in 3 years", "5", "Future care planned")
            )
        ),

        TaskTemplate(
            id = "mammogram",
            name = "Mammogram Screening",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Get mammogram screening",
            description = "Breast cancer screening mammogram",
            avoidanceReason = "Fear of pain from compression, anxiety about potential bad news, embarrassment, putting it off",
            benefits = "Early breast cancer detection, peace of mind, potentially life-saving, following recommended guidelines",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Schedule mammogram appointment", "10", "Date on calendar"),
                SubtaskTemplateData("Schedule for week after period (less tender)", "5", "Optimal timing chosen"),
                SubtaskTemplateData("Avoid caffeine day before (reduces tenderness)", "5", "Body prepared"),
                SubtaskTemplateData("Don't wear deodorant/lotion day of", "2", "Ready for accurate imaging"),
                SubtaskTemplateData("Attend mammogram (15-20 min)", "30", "Screening complete!"),
                SubtaskTemplateData("Wait for results and follow up if needed", "10", "Results reviewed")
            )
        ),

        TaskTemplate(
            id = "prostate_exam",
            name = "Prostate Exam (DRE)",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Get prostate screening exam",
            description = "Digital rectal exam (DRE) for prostate cancer screening",
            avoidanceReason = "Embarrassment, discomfort with the procedure, putting off an awkward exam, fear of results",
            benefits = "Early prostate cancer detection, potentially life-saving, peace of mind, following screening guidelines",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Discuss screening options with doctor", "15", "Know what to expect"),
                SubtaskTemplateData("Schedule appointment", "10", "Date set"),
                SubtaskTemplateData("Remember it's quick (under 1 minute)", "2", "Mentally prepared"),
                SubtaskTemplateData("Attend appointment and exam", "30", "Exam complete!"),
                SubtaskTemplateData("Discuss results with doctor", "10", "Results understood"),
                SubtaskTemplateData("Schedule follow-up if recommended", "5", "Next steps planned")
            )
        ),

        TaskTemplate(
            id = "sti_screening",
            name = "STI Screening",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Get STI screening tests",
            description = "Sexually transmitted infection screening and testing",
            avoidanceReason = "Embarrassment about sexual history, fear of judgment, anxiety about results, stigma",
            benefits = "Know your status, protect your health and partners, early treatment if needed, peace of mind",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Find confidential testing location", "10", "Location identified"),
                SubtaskTemplateData("Schedule appointment or walk-in", "10", "Ready to go"),
                SubtaskTemplateData("Remember testing is confidential and routine", "5", "Anxiety reduced"),
                SubtaskTemplateData("Complete testing (blood/urine/swab)", "30", "Testing done!"),
                SubtaskTemplateData("Get results (call or portal)", "10", "Results received"),
                SubtaskTemplateData("Follow treatment plan if needed", "15", "Health protected"),
                SubtaskTemplateData("Discuss prevention with partner", "20", "Communication complete")
            )
        ),

        TaskTemplate(
            id = "mri_ct_scan",
            name = "MRI or CT Scan",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Complete MRI or CT imaging scan",
            description = "Diagnostic imaging scan ordered by doctor",
            avoidanceReason = "Claustrophobia in enclosed MRI machine, anxiety about radiation (CT), fear of what might be found",
            benefits = "Accurate diagnosis, guide treatment decisions, rule out serious conditions, get answers",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Get imaging order from doctor", "10", "Order obtained"),
                SubtaskTemplateData("Schedule scan at imaging center", "15", "Appointment made"),
                SubtaskTemplateData("Follow prep instructions (fasting, etc.)", "30", "Properly prepared"),
                SubtaskTemplateData("Remove all metal objects before scan", "5", "Ready for imaging"),
                SubtaskTemplateData("Ask for anti-anxiety medication if needed", "10", "Calming support arranged"),
                SubtaskTemplateData("Complete the scan", "45", "Scan finished!"),
                SubtaskTemplateData("Follow up with doctor on results", "20", "Results discussed")
            )
        ),

        TaskTemplate(
            id = "surgery_prep",
            name = "Prepare for Surgery",
            category = TaskTemplateCategory.MEDICAL,
            taskName = "Prepare for upcoming surgical procedure",
            description = "Complete all pre-surgery requirements and preparations",
            avoidanceReason = "Fear of anesthesia, anxiety about outcomes, worried about pain or complications, overwhelmed by prep",
            benefits = "Best possible outcome, reduced complications, peace of mind, organized recovery, procedure gets done",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Attend pre-op consultation", "60", "Know what to expect"),
                SubtaskTemplateData("Complete required lab work and tests", "45", "Medical clearance obtained"),
                SubtaskTemplateData("Arrange time off work for recovery", "15", "Schedule cleared"),
                SubtaskTemplateData("Arrange transportation and post-op help", "20", "Support lined up"),
                SubtaskTemplateData("Prepare home for recovery (supplies, etc.)", "60", "Recovery space ready"),
                SubtaskTemplateData("Follow pre-surgery instructions (fasting, etc.)", "30", "Properly prepared"),
                SubtaskTemplateData("Attend surgery", "240", "Procedure complete!")
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
        ),

        // App Development Templates
        TaskTemplate(
            id = "play_store_release",
            name = "Prepare for Play Store Release",
            category = TaskTemplateCategory.APP_DEVELOPER,
            taskName = "Launch app on Google Play Store",
            description = "Complete all requirements to publish your app on the Play Store",
            avoidanceReason = "Overwhelming number of steps, perfectionism paralysis, fear of rejection, easier to keep tweaking features",
            benefits = "App reaches users, potential revenue, portfolio piece, sense of accomplishment, real-world feedback",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Create app icons (all required sizes)", "60", "Professional branding ready"),
                SubtaskTemplateData("Take screenshots for all device types", "45", "Visual assets complete"),
                SubtaskTemplateData("Write app description and feature list", "30", "Compelling store listing"),
                SubtaskTemplateData("Create privacy policy page/document", "60", "Legal requirement met"),
                SubtaskTemplateData("Generate signed release APK/AAB", "30", "Production build ready"),
                SubtaskTemplateData("Create developer account (\$25 one-time)", "15", "Account active"),
                SubtaskTemplateData("Fill out store listing details", "45", "All fields completed"),
                SubtaskTemplateData("Submit for review", "10", "App submitted!"),
                SubtaskTemplateData("Monitor review status and respond", "20", "Published!")
            )
        ),

        TaskTemplate(
            id = "privacy_policy",
            name = "Write Privacy Policy",
            category = TaskTemplateCategory.APP_DEVELOPER,
            taskName = "Create app privacy policy",
            description = "Write comprehensive privacy policy for your app",
            avoidanceReason = "Legal jargon is intimidating, not sure what to include, boring administrative task",
            benefits = "Play Store requirement met, user trust, legal protection, professional appearance",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("List all data your app collects", "20", "Data inventory complete"),
                SubtaskTemplateData("Document how data is used", "15", "Usage clearly defined"),
                SubtaskTemplateData("Identify third-party services (ads, analytics)", "10", "Partners documented"),
                SubtaskTemplateData("Use privacy policy generator tool", "30", "Draft policy created"),
                SubtaskTemplateData("Review and customize generated policy", "30", "Policy personalized"),
                SubtaskTemplateData("Host policy on website or app", "20", "Policy accessible"),
                SubtaskTemplateData("Add policy link to app and store listing", "10", "Compliance complete")
            )
        ),

        TaskTemplate(
            id = "app_screenshots",
            name = "Create Store Screenshots & Graphics",
            category = TaskTemplateCategory.APP_DEVELOPER,
            taskName = "Design app store visual assets",
            description = "Create compelling screenshots and promotional graphics for app store",
            avoidanceReason = "Design work is time-consuming, perfectionism, not a designer, hard to showcase features",
            benefits = "Higher conversion rate, professional appearance, showcases best features, competitive advantage",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Identify 3-5 key features to showcase", "15", "Messaging focused"),
                SubtaskTemplateData("Take clean screenshots on different devices", "30", "Raw assets captured"),
                SubtaskTemplateData("Add captions/annotations to screenshots", "45", "Features highlighted"),
                SubtaskTemplateData("Create feature graphic (1024x500)", "60", "Header banner ready"),
                SubtaskTemplateData("Design promo graphic if needed", "45", "Marketing asset complete"),
                SubtaskTemplateData("Optimize images for size and clarity", "20", "Assets optimized"),
                SubtaskTemplateData("Upload to Play Store console", "15", "Visuals live!")
            )
        ),

        TaskTemplate(
            id = "app_analytics",
            name = "Set Up App Analytics",
            category = TaskTemplateCategory.APP_DEVELOPER,
            taskName = "Implement analytics and crash reporting",
            description = "Add analytics and error tracking to understand user behavior and fix crashes",
            avoidanceReason = "Feels like extra work, worried about privacy implications, SDK integration seems complex",
            benefits = "Understand how users interact, catch crashes early, data-driven decisions, improve retention",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Choose analytics service (Firebase, etc.)", "15", "Tool selected"),
                SubtaskTemplateData("Create account and add app", "20", "Account configured"),
                SubtaskTemplateData("Integrate SDK into project", "45", "SDK installed"),
                SubtaskTemplateData("Add key event tracking", "30", "Important actions tracked"),
                SubtaskTemplateData("Set up crash reporting", "30", "Crashes will be reported"),
                SubtaskTemplateData("Update privacy policy for analytics", "20", "Privacy updated"),
                SubtaskTemplateData("Test analytics in debug build", "20", "Confirmed working"),
                SubtaskTemplateData("Deploy and monitor dashboard", "15", "Data flowing!")
            )
        ),

        TaskTemplate(
            id = "first_app_update",
            name = "Ship First App Update",
            category = TaskTemplateCategory.APP_DEVELOPER,
            taskName = "Release first update after launch",
            description = "Push your first post-launch update with bug fixes and improvements",
            avoidanceReason = "App seems fine as-is, scared to break things, unsure about versioning, analyzing feedback is hard",
            benefits = "Show users you're maintaining the app, fix reported issues, build user trust, improve ratings",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Review user feedback and crash reports", "30", "Issues identified"),
                SubtaskTemplateData("Prioritize top 3-5 fixes/improvements", "15", "Focus areas chosen"),
                SubtaskTemplateData("Implement fixes and test thoroughly", "120", "Updates coded and tested"),
                SubtaskTemplateData("Increment version number appropriately", "5", "Version bumped"),
                SubtaskTemplateData("Write release notes for users", "15", "Changelog ready"),
                SubtaskTemplateData("Generate signed release build", "20", "Update package ready"),
                SubtaskTemplateData("Upload to Play Store as update", "15", "Update submitted"),
                SubtaskTemplateData("Monitor rollout and new crash reports", "20", "Update shipped!")
            )
        ),

        TaskTemplate(
            id = "update_github_pat",
            name = "Update GitHub PAT",
            category = TaskTemplateCategory.APP_DEVELOPER,
            taskName = "Rotate GitHub Personal Access Token",
            description = "Update expiring GitHub Personal Access Token before it breaks workflows",
            avoidanceReason = "Seems complicated, worried about breaking things, ignoring expiration emails, not sure where tokens are used",
            benefits = "Avoid broken git workflows, maintain CI/CD pipelines, security best practice, peace of mind",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Go to GitHub Settings > Developer Settings", "5", "Settings page open"),
                SubtaskTemplateData("Navigate to Personal Access Tokens", "5", "Token page loaded"),
                SubtaskTemplateData("Generate new token with same scopes", "10", "New token created"),
                SubtaskTemplateData("Copy token immediately (shown once!)", "2", "Token safely copied"),
                SubtaskTemplateData("Update in git credential manager", "10", "Local git updated"),
                SubtaskTemplateData("Update in CI/CD secrets (GitHub Actions, etc.)", "15", "CI/CD still works"),
                SubtaskTemplateData("Update in any automation scripts", "15", "Scripts updated"),
                SubtaskTemplateData("Test git push/pull to verify", "5", "Confirmed working"),
                SubtaskTemplateData("Delete old expired token", "5", "Cleanup complete!")
            )
        ),

        // Christmas Templates
        TaskTemplate(
            id = "wrap_presents",
            name = "Wrap All Presents",
            category = TaskTemplateCategory.CHRISTMAS,
            taskName = "Wrap All Christmas Presents",
            description = "Get all your gifts wrapped and ready to go under the tree",
            avoidanceReason = "Wrapping feels tedious and time-consuming, don't have supplies ready, worried about doing it poorly",
            benefits = "Gifts look thoughtful and festive, saves time on Christmas morning, reduces holiday stress, satisfying to see everything done",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Gather wrapping supplies (paper, tape, scissors, tags)", "10", "Everything in one spot"),
                SubtaskTemplateData("Sort gifts by recipient", "5", "Organized and ready"),
                SubtaskTemplateData("Wrap first batch of gifts", "20", "Making great progress"),
                SubtaskTemplateData("Wrap remaining gifts", "20", "Almost done!"),
                SubtaskTemplateData("Create and attach gift tags", "10", "All presents wrapped and labeled!")
            )
        ),

        TaskTemplate(
            id = "holiday_cards",
            name = "Send Holiday Cards",
            category = TaskTemplateCategory.CHRISTMAS,
            taskName = "Send Holiday Cards",
            description = "Write and mail holiday cards to friends and family",
            avoidanceReason = "Feels like a lot of work, addressing envelopes is boring, not sure what to write",
            benefits = "Maintains relationships, spreads holiday cheer, people genuinely appreciate the thought, nice tradition",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Buy or find holiday cards", "15", "Cards ready to go"),
                SubtaskTemplateData("Write out address list", "10", "Know who you're sending to"),
                SubtaskTemplateData("Write personal messages in cards", "30", "Heartfelt messages complete"),
                SubtaskTemplateData("Address and stamp envelopes", "15", "Ready for the mail"),
                SubtaskTemplateData("Mail cards at post office", "10", "Holiday cheer sent!")
            )
        ),

        TaskTemplate(
            id = "bake_cookies",
            name = "Bake Holiday Cookies",
            category = TaskTemplateCategory.CHRISTMAS,
            taskName = "Bake Holiday Cookies",
            description = "Bake a batch of festive cookies for the season",
            avoidanceReason = "Cleanup is messy, seems time-intensive, worried about them turning out poorly",
            benefits = "Delicious treats to enjoy, fun activity, great for sharing with others, house smells amazing",
            isRepeating = false,
            subtasks = listOf(
                SubtaskTemplateData("Get ingredients from store", "20", "Everything ready to bake"),
                SubtaskTemplateData("Prepare workspace and gather tools", "10", "Kitchen set up"),
                SubtaskTemplateData("Mix and bake first batch", "30", "First cookies in the oven"),
                SubtaskTemplateData("Bake remaining batches", "30", "All cookies baked"),
                SubtaskTemplateData("Decorate cookies", "20", "Beautiful festive cookies!")
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
