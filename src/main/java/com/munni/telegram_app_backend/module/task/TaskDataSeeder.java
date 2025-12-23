//package com.munni.telegram_app_backend.module.task;
//
//import com.munni.telegram_app_backend.enums.TaskStatus;
//import com.munni.telegram_app_backend.enums.TaskType;
//import com.munni.telegram_app_backend.module.task.Task;
//import com.munni.telegram_app_backend.module.task.TaskRepository;
//import com.munni.telegram_app_backend.module.user.User;
//import com.munni.telegram_app_backend.module.user.UserRepo;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class TaskDataSeeder implements CommandLineRunner {
//
//    private final TaskRepository taskRepository;
//    private final UserRepo userRepository;
//
//    @Override
//    public void run(String... args) {
//        // Only seed if tasks table is empty
//        if (taskRepository.count() == 0) {
//            log.info("Seeding sample tasks...");
//            seedSampleTasks();
//        }
//    }
//
//    private void seedSampleTasks() {
//        // Get all users to create tasks for them
//        List<User> users = userRepository.findAll();
//
//        if (users.isEmpty()) {
//            log.warn("No users found. Tasks will be created when users register.");
//            return;
//        }
//
//        // Real tasks with actual channels and links
//        List<TaskData> sampleTasks = createSampleTaskData();
//
//        for (User user : users) {
//            for (TaskData taskData : sampleTasks) {
//                Task task = Task.builder()
//                        .user(user)
//                        .taskType(taskData.taskType)
//                        .taskTitle(taskData.taskTitle)
//                        .taskDescription(taskData.taskDescription)
//                        .taskLink(taskData.taskLink)
//                        .rewardAmount(taskData.rewardAmount)
//                        .status(TaskStatus.PENDING)
//                        .build();
//
//                taskRepository.save(task);
//            }
//
//            // Update user's total tasks count
//            user.setTotalTasks(user.getTotalTasks() + sampleTasks.size());
//            userRepository.save(user);
//        }
//
//        log.info("Successfully seeded {} tasks for {} users", sampleTasks.size(), users.size());
//    }
//
//    private List<TaskData> createSampleTaskData() {
//        List<TaskData> tasks = new ArrayList<>();
//
//        // Telegram Channel Tasks
//        tasks.add(new TaskData(
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Telegram Official Channel",
//                "Join the official Telegram channel for latest updates and announcements",
//                "https://t.me/telegram",
//                new BigDecimal("2.50")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Durov's Channel",
//                "Follow Pavel Durov's official channel for insights and news",
//                "https://t.me/durov",
//                new BigDecimal("3.00")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Telegram Tips",
//                "Get tips and tricks about using Telegram effectively",
//                "https://t.me/TelegramTips",
//                new BigDecimal("2.00")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Crypto News Channel",
//                "Stay updated with the latest cryptocurrency news and market trends",
//                "https://t.me/crypto",
//                new BigDecimal("3.50")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.TELEGRAM_CHANNEL,
//                "Join Tech News",
//                "Get the latest technology news and updates",
//                "https://t.me/TechCrunch",
//                new BigDecimal("2.50")
//        ));
//
//        // YouTube Tasks
//        tasks.add(new TaskData(
//                TaskType.YOUTUBE,
//                "Subscribe to Telegram's YouTube",
//                "Subscribe to Telegram's official YouTube channel",
//                "https://www.youtube.com/@telegram",
//                new BigDecimal("4.00")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.YOUTUBE,
//                "Watch Telegram Features Video",
//                "Watch and learn about amazing Telegram features",
//                "https://www.youtube.com/watch?v=gSVvxOchT8Y",
//                new BigDecimal("3.50")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.YOUTUBE,
//                "Subscribe to Tech Channel",
//                "Subscribe to Marques Brownlee for tech reviews",
//                "https://www.youtube.com/@mkbhd",
//                new BigDecimal("4.50")
//        ));
//
//        // Custom Link Tasks
//        tasks.add(new TaskData(
//                TaskType.CUSTOM_LINK,
//                "Follow on Twitter/X",
//                "Follow Telegram on Twitter for social updates",
//                "https://twitter.com/telegram",
//                new BigDecimal("2.00")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.CUSTOM_LINK,
//                "Visit Telegram Website",
//                "Visit the official Telegram website and explore features",
//                "https://telegram.org",
//                new BigDecimal("1.50")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.CUSTOM_LINK,
//                "Read Telegram FAQ",
//                "Read the Telegram FAQ to learn more about the platform",
//                "https://telegram.org/faq",
//                new BigDecimal("2.50")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.CUSTOM_LINK,
//                "Explore Telegram Apps",
//                "Check out all available Telegram apps for different platforms",
//                "https://telegram.org/apps",
//                new BigDecimal("2.00")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.CUSTOM_LINK,
//                "Follow on Instagram",
//                "Follow Telegram on Instagram for visual updates",
//                "https://instagram.com/telegram",
//                new BigDecimal("2.00")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.CUSTOM_LINK,
//                "Join Telegram Community",
//                "Visit Telegram's community page and connect with users",
//                "https://telegram.org/blog",
//                new BigDecimal("1.50")
//        ));
//
//        tasks.add(new TaskData(
//                TaskType.CUSTOM_LINK,
//                "Check Telegram GitHub",
//                "Visit Telegram's GitHub repository for open source projects",
//                "https://github.com/telegramdesktop",
//                new BigDecimal("3.00")
//        ));
//
//        return tasks;
//    }
//
//    // Helper class to store task data
//    private static class TaskData {
//        TaskType taskType;
//        String taskTitle;
//        String taskDescription;
//        String taskLink;
//        BigDecimal rewardAmount;
//
//        public TaskData(TaskType taskType, String taskTitle, String taskDescription,
//                       String taskLink, BigDecimal rewardAmount) {
//            this.taskType = taskType;
//            this.taskTitle = taskTitle;
//            this.taskDescription = taskDescription;
//            this.taskLink = taskLink;
//            this.rewardAmount = rewardAmount;
//        }
//    }
//}