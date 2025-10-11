package software.plusminus.spring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
public class SpringUtilInitializer implements BeanPostProcessor {

    @SuppressWarnings("PMD.UnusedPrivateField")
    @SuppressFBWarnings("URF_UNREAD_FIELD")
    private SpringUtil springUtil;

}
