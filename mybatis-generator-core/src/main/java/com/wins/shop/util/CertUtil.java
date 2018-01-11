/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       证书工具类.
 * =============================================================================
 */
package com.wins.shop.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.util.ResourceUtils;

import com.wins.shop.util.StringUtil;

public class CertUtil {
	private static final Logger logger = Logger.getLogger(CertUtil.class);

	/** 证书容器. */
	private static KeyStore keyStore = null;
	/** 密码加密证书 */
	@SuppressWarnings("unused")
	private static X509Certificate encryptCert = null;
	// /** 磁道加密证书 */
	// private static X509Certificate encryptTrackCert = null;
	/** 磁道加密公钥 */
	// private static PublicKey encryptTrackKey = null;

	/** 验证签名证书. */
	private static X509Certificate validateCert = null;
	/** 验签证书存储Map. */
	private static Map<String, X509Certificate> certMap = new HashMap<String, X509Certificate>();
	/** 根据传入证书文件路径和密码读取指定的证书容器.(一种线程安全的实现方式) */
	private final static ThreadLocal<KeyStore> certKeyStoreLocal = new ThreadLocal<KeyStore>();
	/** 基于Map存储多商户RSA私钥 */
	@SuppressWarnings("unused")
	private final static Map<String, KeyStore> certKeyStoreMap = new ConcurrentHashMap<String, KeyStore>();

	static {
		init();
	}

	/**
	 * 初始化所有证书.
	 */
	public static void init() {

		// 单证书模式,初始化配置文件中的签名证书
		initSignCert();

		initEncryptCert();// 初始化加密公钥证书
		// initTrackKey();
		initValidateCertFromDir();// 初始化所有的验签证书
	}

	/**
	 * 加载签名证书
	 */
	public static void initSignCert() {
		if (null != keyStore) {
			keyStore = null;
		}
		try {
			// CertUtil.class.getResourceAsStream("/certs/acp_test_enc.cer")
			// "D:/certs/acp_test_sign.pfx"
			keyStore = getKeyInfo(CertUtil.class.getResourceAsStream("/certs/unionpay/acp_test_sign.pfx"), "098123",
					"PKCS12");
		} catch (IOException e) {
			// LogUtil.writeErrorLog("InitSignCert Error", e);
		}
	}

	/**
	 * 根据传入的证书文件路径和证书密码加载指定的签名证书
	 * 
	 * @deprecated
	 */
	public static void initSignCert(InputStream in, String certPwd) {
		//// LogUtil.writeLog("加载证书文件[" + certFilePath + "]和证书密码[" + certPwd+
		//// "]的签名证书开始.");
		certKeyStoreLocal.remove();

		try {
			certKeyStoreLocal.set(getKeyInfo(in, certPwd, "PKCS12"));
		} catch (IOException e) {
			//// LogUtil.writeErrorLog("加载签名证书失败", e);
		}
		//// LogUtil.writeLog("加载证书文件[" + certFilePath + "]和证书密码[" + certPwd+
		//// "]的签名证书结束.");
		logger.debug("加载证书文件[" + "]和证书密码[" + certPwd + "]的签名证书结束.");
	}

	/**
	 * 加载密码加密证书 目前支持有两种加密
	 */
	private static void initEncryptCert() {

		encryptCert = initCert(CertUtil.class.getResourceAsStream("/certs/unionpay/acp_test_enc.cer"));
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	private static X509Certificate initCert(InputStream in) {
		X509Certificate encryptCertTemp = null;
		CertificateFactory cf = null;
		// FileInputStream in = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			// in = new FileInputStream(path);
			encryptCertTemp = (X509Certificate) cf.generateCertificate(in);
			// 打印证书加载信息,供测试阶段调试
			// LogUtil.writeLog("[" + path + "][CertId="+
			// encryptCertTemp.getSerialNumber().toString() + "]");
			logger.debug("[" + "][CertId=" + encryptCertTemp.getSerialNumber().toString() + "]");
		} catch (CertificateException e) {
			// LogUtil.writeErrorLog("InitCert Error", e);
			e.printStackTrace();
			logger.debug("InitCert Error");
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					// LogUtil.writeErrorLog(e.toString());
				}
			}
		}
		return encryptCertTemp;
	}

	/**
	 * 从指定目录下加载验证签名证书
	 * 
	 */
	private static void initValidateCertFromDir() {
		certMap.clear();
		CertificateFactory cf = null;
		FileInputStream in = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			File fileDir = ResourceUtils.getFile("classpath:certs/unionpay");
			File[] files = fileDir.listFiles(new CerFilter());
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				in = new FileInputStream(file.getAbsolutePath());
				validateCert = (X509Certificate) cf.generateCertificate(in);
				certMap.put(validateCert.getSerialNumber().toString(), validateCert);
				// 打印证书加载信息,供测试阶段调试
				// LogUtil.writeLog("[" + file.getAbsolutePath() + "][CertId="+
				// validateCert.getSerialNumber().toString() + "]");
				logger.debug(
						"[" + file.getAbsolutePath() + "][CertId=" + validateCert.getSerialNumber().toString() + "]");
			}
			// LogUtil.writeLog("LoadVerifyCert Successful");
		} catch (CertificateException e) {
			// LogUtil.writeErrorLog("LoadVerifyCert Error", e);
		} catch (FileNotFoundException e) {
			// LogUtil.writeErrorLog("LoadVerifyCert Error File Not Found", e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					// LogUtil.writeErrorLog(e.toString());
				}
			}
		}
	}

	/**
	 * 获取签名证书私钥（单证书模式）
	 * 
	 * @return
	 */
	public static PrivateKey getSignCertPrivateKey() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, "098123".toCharArray());
			return privateKey;
		} catch (KeyStoreException e) {
			// LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			logger.debug(e.toString());
			return null;
		} catch (UnrecoverableKeyException e) {
			// LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			logger.debug(e.toString());
			return null;
		} catch (NoSuchAlgorithmException e) {
			// LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			logger.debug(e.toString());
			return null;
		}
	}

	/**
	 * 验证签名证书
	 * 
	 * @return 验证签名证书的公钥
	 */
	public static PublicKey getValidateKey() {
		if (null == validateCert) {
			return null;
		}
		return validateCert.getPublicKey();
	}

	/**
	 * 通过certId获取证书Map中对应证书的公钥
	 * 
	 * @param certId
	 *            证书物理序号
	 * @return 通过证书编号获取到的公钥
	 */
	public static PublicKey getValidateKey(String certId) {
		X509Certificate cf = null;
		if (certMap.containsKey(certId)) {
			// 存在certId对应的证书对象
			cf = certMap.get(certId);
			return cf.getPublicKey();
		} else {
			// 不存在则重新Load证书文件目录
			initValidateCertFromDir();
			if (certMap.containsKey(certId)) {
				// 存在certId对应的证书对象
				cf = certMap.get(certId);
				return cf.getPublicKey();
			} else {
				// LogUtil.writeErrorLog("缺少certId=[" + certId + "]对应的验签证书.");
				return null;
			}
		}
	}

	/**
	 * 获取签名证书中的证书序列号（单证书）
	 * 
	 * @return 证书的物理编号
	 */
	public static String getSignCertId() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			X509Certificate cert = (X509Certificate) keyStore.getCertificate(keyAlias);
			return cert.getSerialNumber().toString();
		} catch (Exception e) {
			// LogUtil.writeErrorLog("getSignCertId Error", e);
			return null;
		}
	}

	/**
	 * 获取签名证书公钥对象
	 * 
	 * @return
	 */
	public static PublicKey getSignPublicKey() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) // we are readin just one
			{
				keyAlias = (String) aliasenum.nextElement();
			}
			Certificate cert = keyStore.getCertificate(keyAlias);
			PublicKey pubkey = cert.getPublicKey();
			return pubkey;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将证书文件读取为证书存储对象
	 * 
	 * @param pfxkeyfile
	 *            证书文件名
	 * @param keypwd
	 *            证书密码
	 * @param type
	 *            证书类型
	 * @return 证书对象
	 * @throws IOException
	 */
	public static KeyStore getKeyInfo(InputStream in, String keypwd, String type) throws IOException {
		//// LogUtil.writeLog("加载签名证书==>" + pfxkeyfile);
		try {
			KeyStore ks = null;
			if ("JKS".equals(type)) {
				ks = KeyStore.getInstance(type);
			} else if ("PKCS12".equals(type)) {
				String jdkVendor = System.getProperty("java.vm.vendor");
				if (null != jdkVendor && jdkVendor.startsWith("IBM")) {
					// 如果使用IBMJDK,则强制设置BouncyCastleProvider的指定位置,解决使用IBMJDK时兼容性问题
					Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
				} else {
					Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				}
				ks = KeyStore.getInstance(type);
			}
			char[] nPassword = null;
			nPassword = null == keypwd || "".equals(keypwd.trim()) ? null : keypwd.toCharArray();
			if (null != ks) {
				ks.load(in, nPassword);
			}
			logger.debug("加载证书成功！");
			return ks;
		} catch (Exception e) {
			if (Security.getProvider("BC") == null) {
			}
			if ((e instanceof KeyStoreException) && "PKCS12".equals(type)) {
				Security.removeProvider("BC");
			}
			return null;
		} finally {
			if (null != in)
				in.close();
		}
	}

	public static void printProviders() {
		//// LogUtil.writeLog("Providers List:");
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			//// LogUtil.writeLog(i + 1 + "." + providers[i].getName());
		}
	}

	/**
	 * 证书文件过滤器
	 * 
	 */
	static class CerFilter implements FilenameFilter {
		public boolean isCer(String name) {
			if (name.toLowerCase().endsWith(".cer")) {
				return true;
			} else {
				return false;
			}
		}

		public boolean accept(File dir, String name) {
			return isCer(name);
		}
	}

	/**
	 * 获取证书容器
	 * 
	 * @return
	 */
	public static Map<String, X509Certificate> getCertMap() {
		return certMap;
	}

	/**
	 * 设置证书容器
	 * 
	 * @param certMap
	 */
	public static void setCertMap(Map<String, X509Certificate> certMap) {
		CertUtil.certMap = certMap;
	}

	/**
	 * 使用模和指数生成RSA公钥 注意：此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同
	 * 
	 * @param modulus
	 *            模
	 * @param exponent
	 *            指数
	 * @return
	 */
	public static PublicKey getPublicKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			//// LogUtil.writeErrorLog("构造RSA公钥失败：" + e);
			return null;
		}
	}

	/**
	 * 使用模和指数的方式获取公钥对象
	 * 
	 * @return
	 */
	public static PublicKey getEncryptTrackCertPublicKey(String modulus, String exponent) {
		if (StringUtil.isEmptyString(modulus) || StringUtil.isEmptyString(exponent)) {
			//// LogUtil.writeErrorLog("[modulus] OR [exponent] invalid");
			return null;
		}
		return getPublicKey(modulus, exponent);
	}

}
