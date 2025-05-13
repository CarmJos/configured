package cc.carm.lib.configuration.value.text.tests.conf;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.annotation.ConfigPath;

@ConfigPath(root = true)
public interface AppMessages extends Configuration {

    ConfiguredMsg WELCOME = ConfiguredMsg.builder()
        .defaults(
            "Hello, %(name)",
            "#more-creating#{1}",
            "This is a test message",
            "#guidance#",
            "{- }#websites#{0,1}",
            "Thanks for your reading!")
        .optional("guidance", "To get more information for %(name), see:")
        .params("name").build();

    ConfiguredMsg NO_PERMISSION = ConfiguredMsg.builder()
        .defaults("Sorry! But you don't have permissions to do this.")
        .build();

    ConfiguredMsg NOT_AVAILABLE = ConfiguredMsg.builder()
        .defaults("Error! Service is not available now.", "Please contact your system manager.")
        .build();

}
