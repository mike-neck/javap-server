import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

public class KotlinConfigure {
    @NotNull
    static TaskCollection<KotlinCompile> compileTasks(@NotNull Project project) {
        return project.getTasks().withType(KotlinCompile.class);
    }
}
