/**
 *    Copyright 2006-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerUtil {

	/**
	 * 将模板文件生成静态HTML文件
	 * @param templatePath
	 *            模板文件存放路径
	 * @param templateName
	 *            模板文件名称
	 * @param fileName
	 *            生成的文件名称
	 * @param root
	 */
	public static void analysisTemplate(String templatePath, String templateName, String fileName, Map<?, ?> root) {
		FileOutputStream fos = null;
		Writer out = null;
		try {
			
			Configuration config = new Configuration();
			// 设置要解析的模板所在的目录，并加载模板文件
			config.setDirectoryForTemplateLoading(new File(templatePath));
			// 设置包装器，并将对象包装为数据模型
			config.setObjectWrapper(new DefaultObjectWrapper());

			// 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
			// 否则会出现乱码
			Template template = config.getTemplate(templateName, "UTF-8");
			// 合并数据模型与模板
			fos = new FileOutputStream(templatePath+File.separator+".."+File.separator+fileName);
			out = new OutputStreamWriter(fos, "UTF-8");
			template.process(root, out);

		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
		} catch (TemplateException e) {
//			logger.error(e.getMessage(), e);
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
//					logger.error(e.getMessage(),e);
				}
			}
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
//					logger.error(e.getMessage(),e);
				}
			}
		}
	}

	/**
	 * 将模板文件生成String
	 * @param templatePath
	 *            模板文件存放路径
	 * @param templateName
	 *            模板文件名称
	 * @param root
	 */
	public static String getStringFromTemplate(String templatePath, String templateName, Map<?, ?> root) throws Exception {
		return getStringFromTemplate(templatePath, templateName, root, false);
	}

	/**
	 * 将模板文件生成String
	 * @param templatePath
	 *            模板文件存放路径
	 * @param templateName
	 *            模板文件名称
	 * @param root
	 */
	public static String getStringFromTemplate(String templatePath, String templateName, Map<?, ?> root,
											   boolean useRelatResource) throws Exception {
		StringWriter sw = new StringWriter();
		Writer out = null;
		try {

			Configuration config = new Configuration();
			// 设置要解析的模板所在的目录，并加载模板文件
			System.out.println("templatePath:" + templatePath + "templateName:" + templateName);
			//如果在jar包内部的
			if (useRelatResource) {
				config.setClassForTemplateLoading(FreeMarkerUtil.class, templatePath);
			} else {
				config.setDirectoryForTemplateLoading(new File(templatePath));
			}
			// 设置包装器，并将对象包装为数据模型
			config.setObjectWrapper(new DefaultObjectWrapper());

			// 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
			// 否则会出现乱码
			Template template = config.getTemplate(templateName, "UTF-8");
			// 合并数据模型与模板
			template.process(root, sw);
			return sw.toString();
		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
			throw e;
		} catch (TemplateException e) {
//			logger.error(e.getMessage(), e);
			throw e;
		} finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
//					logger.error(e.getMessage(),e);
				}
			}
			if(sw != null){
				try {
					sw.close();
				} catch (IOException e) {
//					logger.error(e.getMessage(),e);
				}
			}
		}
	}

}
