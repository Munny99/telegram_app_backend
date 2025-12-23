//package com.munni.telegram_app_backend.module.task;
//
//import com.munni.telegram_app_backend.enums.TaskStatus;
//import com.munni.telegram_app_backend.enums.TaskType;
//import com.munni.telegram_app_backend.module.user.User;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class TaskInitializationService {
//
//    private final TaskRepository taskRepository;
//
//    /**
//     * Initialize default tasks for a new user
//     * Call this method when a new user is created
//     */
//    @Transactional
//    public void initializeTasksForNewUser(User user) {
//        log.info("Initializing tasks for new user: {}", user.getTelegramId());
//
//        List<Task> defaultTasks = createDefaultTasks(user);
//        taskRepository.saveAll(defaultTasks);
//
//        log.info("Created {} tasks for user {}", defaultTasks.size(), user.getTelegramId());
//    }
//
//    private List<Task> createDefaultTasks(User user) {
//        List<Task> tasks = new ArrayList<>();
//
//        // Telegram Channel Tasks
//        tasks.add(createTask(user,
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Telegram Official Channel",
//                "Join the official Telegram channel for latest updates and announcements",
//                "https://t.me/telegram",
//                new BigDecimal("2.50")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Durov's Channel",
//                "Follow Pavel Durov's official channel for insights and news",
//                "https://t.me/durov",
//                new BigDecimal("3.00")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Telegram Tips",
//                "Get tips and tricks about using Telegram effectively",
//                "https://t.me/TelegramTips",
//                new BigDecimal("2.00")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Crypto News Channel",
//                "Stay updated with the latest cryptocurrency news and market trends",
//                "https://t.me/crypto",
//                new BigDecimal("3.50")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Tech News",
//                "Get the latest technology news and updates",
//                "https://t.me/TechCrunch",
//                new BigDecimal("2.50")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Bangladesh Tech Community",
//                "Connect with tech enthusiasts in Bangladesh",
//                "https://t.me/BDTechCommunity",
//                new BigDecimal("2.00")
//        ));
//
//        // YouTube Tasks
//        tasks.add(createTask(user,
//                TaskType.YOUTUBE,
//                "Subscribe to Telegram's YouTube",
//                "Subscribe to Telegram's official YouTube channel",
//                "https://www.youtube.com/@telegram",
//                new BigDecimal("4.00")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.YOUTUBE,
//                "Watch Telegram Features Video",
//                "Watch and learn about amazing Telegram features",
//                "https://www.youtube.com/watch?v=gSVvxOchT8Y",
//                new BigDecimal("3.50")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.YOUTUBE,
//                "Subscribe to MKBHD",
//                "Subscribe to Marques Brownlee for tech reviews",
//                "https://www.youtube.com/@mkbhd",
//                new BigDecimal("4.50")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.YOUTUBE,
//                "Subscribe to Fireship",
//                "Learn programming in 100 seconds with Fireship",
//                "https://www.youtube.com/@Fireship",
//                new BigDecimal("3.00")
//        ));
//
//        // Custom Link Tasks - Social Media
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Follow on Twitter/X",
//                "Follow Telegram on Twitter for social updates",
//                "https://twitter.com/telegram",
//                new BigDecimal("2.00")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Follow on Instagram",
//                "Follow Telegram on Instagram for visual updates",
//                "https://instagram.com/telegram",
//                new BigDecimal("2.00")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Follow on Facebook",
//                "Like Telegram's Facebook page",
//                "https://facebook.com/telegram",
//                new BigDecimal("1.50")
//        ));
//
//        // Custom Link Tasks - Website & Resources
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Visit Telegram Website",
//                "Visit the official Telegram website and explore features",
//                "https://telegram.org",
//                new BigDecimal("1.50")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Read Telegram FAQ",
//                "Read the Telegram FAQ to learn more about the platform",
//                "https://telegram.org/faq",
//                new BigDecimal("2.50")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Explore Telegram Apps",
//                "Check out all available Telegram apps for different platforms",
//                "https://telegram.org/apps",
//                new BigDecimal("2.00")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Read Telegram Blog",
//                "Visit Telegram's blog and read latest articles",
//                "https://telegram.org/blog",
//                new BigDecimal("1.50")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Check Telegram GitHub",
//                "Visit Telegram's GitHub repository for open source projects",
//                "https://github.com/telegramdesktop",
//                new BigDecimal("3.00")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Explore Telegram Stickers",
//                "Browse and add amazing Telegram stickers",
//                "https://telegram.org/blog/stickers-revolution",
//                new BigDecimal("1.00")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Learn About Telegram Bots",
//                "Discover what Telegram bots can do",
//                "https://core.telegram.org/bots",
//                new BigDecimal("2.50")
//        ));
//
//        tasks.add(createTask(user,
//                TaskType.CUSTOM_LINK,
//                "Check Telegram Mini Apps",
//                "Explore Telegram Mini Apps documentation",
//                "https://core.telegram.org/bots/webapps",
//                new BigDecimal("3.00")
//        ));
//
//        return tasks;
//    }
//
//    private Task createTask(User user, TaskType taskType, String title,
//                           String description, String link, BigDecimal reward) {
//        return Task.builder()
//                .user(user)
//                .taskType(taskType)
//                .taskTitle(title)
//                .taskDescription(description)
//                .taskLink(link)
//                .rewardAmount(reward)
//                .status(TaskStatus.PENDING)
//                .build();
//    }
//
//    /**
//     * Get default tasks data (useful for admin panel)
//     */
//    public List<TaskTemplate> getDefaultTaskTemplates() {
//        List<TaskTemplate> templates = new ArrayList<>();
//
//        templates.add(new TaskTemplate(TaskType.TELEGRAM_CHANNEL,
//            "Join Telegram Official Channel",
//            "https://t.me/telegram",
//            new BigDecimal("2.50")));
//
//        templates.add(new TaskTemplate(TaskType.TELEGRAM_CHANNEL,
//            "Join Durov's Channel",
//            "https://t.me/durov",
//            new BigDecimal("3.00")));
//
//        // Add more templates as needed
//
//        return templates;
//    }
//
//    // Template class for task configuration
//    public static class TaskTemplate {
//        public TaskType taskType;
//        public String taskTitle;
//        public String taskLink;
//        public BigDecimal rewardAmount;
//
//        public TaskTemplate(TaskType taskType, String taskTitle,
//                          String taskLink, BigDecimal rewardAmount) {
//            this.taskType = taskType;
//            this.taskTitle = taskTitle;
//            this.taskLink = taskLink;
//            this.rewardAmount = rewardAmount;
//        }
//    }
//}