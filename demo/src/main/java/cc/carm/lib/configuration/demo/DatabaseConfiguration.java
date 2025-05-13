package cc.carm.lib.configuration.demo;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.annotation.ConfigPath;
import cc.carm.lib.configuration.annotation.HeaderComments;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;

@HeaderComments({"", "数据库配置", "  用于提供数据库连接，进行数据库操作。"})
public interface DatabaseConfiguration extends Configuration {

    @ConfigPath("driver")
    @HeaderComments({
        "数据库驱动配置，请根据数据库类型设置。",
        "- MySQL(旧): com.mysql.jdbc.Driver",
        "- MySQL(新): com.mysql.cj.jdbc.Driver",
        "- MariaDB(推荐): org.mariadb.jdbc.Driver",
    })
    ConfiguredValue<String> DRIVER_NAME = ConfiguredValue.of(
        String.class, "com.mysql.cj.jdbc.Driver"
    );

    ConfiguredValue<String> HOST = ConfiguredValue.of(String.class, "127.0.0.1");
    ConfiguredValue<Integer> PORT = ConfiguredValue.of(Integer.class, 3306);
    ConfiguredValue<String> DATABASE = ConfiguredValue.of(String.class, "minecraft");
    ConfiguredValue<String> USERNAME = ConfiguredValue.of(String.class, "root");
    ConfiguredValue<String> PASSWORD = ConfiguredValue.of(String.class, "password");

    ConfiguredValue<String> EXTRA = ConfiguredValue.of(String.class, "?useSSL=false");

    static String buildJDBC() {
        return String.format("jdbc:mysql://%s:%s/%s%s", HOST.get(), PORT.get(), DATABASE.get(), EXTRA.get());
    }

}
