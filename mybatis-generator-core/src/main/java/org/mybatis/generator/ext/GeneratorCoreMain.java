package org.mybatis.generator.ext;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.ext.api.MeldListMergeShellCallback;
import org.mybatis.generator.ext.api.MeldMergeShellCallback;
import org.mybatis.generator.ext.api.ProjectPathShellCallBack;
import org.mybatis.generator.ext.api.data.SourceFileManager;
import org.mybatis.generator.ext.ui.FileCmpPannel;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.util.StringUtil;

import com.alibaba.fastjson.JSON;

/**
 * 主类 用法： java xx -Dgen=generatorConfigExtTest2.xml
 */
public class GeneratorCoreMain {

	private static final String CONFIG_FILE =
			"E:/git_works/boss_code_generator/mybatis-generator-core/src/main/resources/generatorConfig4ibabossBusi.xml";

	public static void main(String[] args) throws Exception {
		List<String> warnings = new ArrayList<String>();
		ConfigurationParser cp = new ConfigurationParser(warnings);

		// Map<String, String> arguments = parseCommandLine(args);
		// if (!arguments.containsKey(CONFIG_FILE)) {
		// writeLine(getString("RuntimeError.0"));
		// return;
		// }

		// String genXmlFile = arguments.containsKey(CONFIG_FILE) ?
		// arguments.get(CONFIG_FILE)
		// : System.getProperty("genXml");
		// File configurationFile = new File(genXmlFile);
		// if (!configurationFile.exists()) {
		// writeLine(getString("RuntimeError.1", genXmlFile)); //$NON-NLS-1$
		// return;
		// }

		File configurationFile = new File(CONFIG_FILE);
		if (!configurationFile.exists()) {
			writeLine(getString("RuntimeError.1", CONFIG_FILE)); //$NON-NLS-1$
			return;
		}

		Configuration config = cp.parseConfiguration(new FileInputStream(configurationFile));
		// 修改支持配置classPathEntry自定义配置 <classPathEntry
		// location="C:/Users/chenzhm/.m2/repository/mysql/mysql-connector-java/5.1.38/mysql-connector-java-5.1.38.jar"
		// />
		if (System.getProperty("classPathEntry") != null) {
			config.getClassPathEntries().clear();
			config.addClasspathEntry(System.getProperty("classPathEntry"));
		}

		// GeneratorCoreMain.class.getClassLoader().getResourceAsStream(genXmlFile));
		String mergeClz = config.getContexts().get(0).getProperty("mergeCallback");
		ShellCallback shellCallback = null;
		if (mergeClz == null) {
			shellCallback = new DefaultShellCallback(true);
		} else {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Constructor<?> c = Class.forName(mergeClz, true, cl).getConstructor(boolean.class);
			shellCallback = (ShellCallback) c.newInstance(true);
		}

		// 增加支持配置是否生成services工具类
		if (System.getProperty("genService") != null) {
			config.getContexts().get(0).getJavaClientGeneratorConfiguration().addProperty("genService",
					System.getProperty("genService").toString());
		}

		// 使用方法： -configfile ${project_loc}/target/classes/generatorConfig.xml
		// 支持外部配置使用自定义配置
		String projectPath = System.getProperty("prjPath");
		String prjPathMapStr = System.getProperty("prjPathMap");

		if (shellCallback instanceof ProjectPathShellCallBack && StringUtil.isEmptyString(projectPath)) {
			projectPath = config.getContexts().get(0).getProperty("projectPath");
			prjPathMapStr = config.getContexts().get(0).getProperty("prjPathMap");
		}

		if (StringUtil.isEmptyString(projectPath) && StringUtil.isEmptyString(prjPathMapStr)) {
			throw new RuntimeException("路径没配置");
		}
		if (StringUtil.isNotEmptyString(projectPath)) {
			((ProjectPathShellCallBack) shellCallback).setProjectPath(projectPath);
		}
		// 支持多个path路径的配置
		if (StringUtil.isNotEmptyString(prjPathMapStr)) {
			Map<String, String> mapProjPath = JSON.parseObject(prjPathMapStr, HashMap.class);
			((ProjectPathShellCallBack) shellCallback).setProjPathMap(mapProjPath);
		}

		if (shellCallback instanceof MeldMergeShellCallback) {
			// 全部文件重排
			String reArgCodeOrder = config.getContexts().get(0).getProperty("reArgCodeOrder");
			if ("true".equals(reArgCodeOrder))
				((MeldMergeShellCallback) shellCallback).setReArgByOldCode(true);
			// 部分文件重排
			String reArgFiles = config.getContexts().get(0).getProperty("reArgFiles");
			if (StringUtil.isNotEmptyString(reArgFiles)) {
				String[] files = StringUtil.split(reArgFiles, ",");
				for (String file : files)
					((MeldMergeShellCallback) shellCallback).addReArgFilePath(file);
			}
			// meld文件路径
			String meldLocation = config.getContexts().get(0).getProperty("meldLocation");
			if (StringUtil.isNotEmptyString(meldLocation)) {
				((MeldMergeShellCallback) shellCallback).setMeldLocation(meldLocation);
			}
		}

		try {
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
			myBatisGenerator.generate(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (shellCallback instanceof MeldListMergeShellCallback) {
			// meld文件路径
			String meldLocation = config.getContexts().get(0).getProperty("meldLocation");
			new FileCmpPannel(SourceFileManager.getInstance().getFilePathCmpList(), meldLocation);
		}
		// assertEquals(1, 1);
	}

	private static Map<String, String> parseCommandLine(String[] args) {
		List<String> errors = new ArrayList<String>();
		Map<String, String> arguments = new HashMap<String, String>();

		for (int i = 0; i < args.length; i++) {
			if (CONFIG_FILE.equalsIgnoreCase(args[i])) {
				if ((i + 1) < args.length) {
					arguments.put(CONFIG_FILE, args[i + 1]);
				} else {
					errors.add(getString("RuntimeError.19", CONFIG_FILE)); //$NON-NLS-1$
				}
				i++;
			}
		}

		if (!errors.isEmpty()) {
			for (String error : errors) {
				writeLine(error);
			}

			System.exit(-1);
		}

		return arguments;
	}

	private static void writeLine(String message) {
		System.out.println(message);
	}

	private static void writeLine() {
		System.out.println();
	}
}
