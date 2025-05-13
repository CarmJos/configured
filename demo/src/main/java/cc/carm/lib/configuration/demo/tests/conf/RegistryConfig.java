package cc.carm.lib.configuration.demo.tests.conf;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.annotation.ConfigPath;
import cc.carm.lib.configuration.annotation.FooterComments;
import cc.carm.lib.configuration.annotation.HeaderComments;
import cc.carm.lib.configuration.annotation.InlineComment;
import cc.carm.lib.configuration.demo.tests.model.UserRecord;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;

import java.util.UUID;

public class RegistryConfig implements Configuration {

    @HeaderComments("Support for configurations as instances")
    public final InstanceConfig INSTANCE = new InstanceConfig();

    @ConfigPath("test.user") // 通过注解规定配置文件中的路径，若不进行注解则以变量名自动生成。
    @FooterComments({"12313213212"})
    @InlineComment(value = "用户名(匹配注释)", regex = "name") // 通过注解给配置添加注释。
    @InlineComment(value = "信息", regex = {"info.*", "info.game.*"}) // 通过注解给配置添加注释。
    public final ConfiguredValue<UserRecord> OWNER = ConfiguredValue.builderOf(UserRecord.class).fromSection()
        .defaults(new UserRecord("Carm", UUID.randomUUID()))
        .parse((holder, section) -> UserRecord.deserialize(section))
        .serialize((holder, data) -> data.serialize()).build();


}
