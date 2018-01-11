package com.wins.shop.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 集合处理工具类，
 * @author Administrator
 *
 */
public class CollUtil {
	private static Logger logger = Logger.getLogger(CollUtil.class);

	/**
	 * 集合分组接口
	 * @param <D> 待分组的结合类型
	 * @param <T> 分组依据字段的数据类型
	 */
	public interface GroupBy<D, T> {
		T groupby(D obj);
	}

	public interface PrimaryKey<D, T> {
		T keyby(D d);
	}

	/**
	 * 集合分组函数
	 * @param colls
	 * @param gb
	 * @return 
	 */
	public static final <T, D> Map<T, List<D>> groupColl(Collection<D> colls, GroupBy<D, T> gb) {
		if (null == colls || colls.isEmpty()) {
			logger.error("传入待分组的集合为空，搞啥咧？");
			return null;
		}
		if (null == gb) {
			logger.error("分组条件接口为空，搞啥咧？");
			return null;
		}
		Iterator<D> iter = colls.iterator();
		Map<T, List<D>> map = new HashMap<T, List<D>>();
		while (iter.hasNext()) {
			D d = iter.next();
			T t = gb.groupby(d);
			if (map.containsKey(t)) {
				map.get(t).add(d);
			} else {
				List<D> list = new ArrayList<D>();
				list.add(d);
				map.put(t, list);
			}
		}
		return map;
	}

	/**
	 * 集合转换成Map结构，对唯一主键的表结构转换成MAP，然后直接根据Key获取对象适用。
	 * @param colls
	 * @param sb
	 * @return
	 */
	public static final <T, D> Map<T, D> coll2Map(Collection<D> colls, PrimaryKey<D, T> sb) {
		if (null == colls || colls.isEmpty()) {
			logger.error("传入待分组的集合为空，搞啥咧？");
			return null;
		}
		Iterator<D> iter = colls.iterator();
		Map<T, D> map = new HashMap<T, D>();
		while (iter.hasNext()) {
			D d = iter.next();
			T t = sb.keyby(d);
			if (map.containsKey(t)) {
				continue;
			} else {
				map.put(t, d);
			}
		}
		return map;
	}

	
	
	//////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////
	public static class InnerClass {
		Integer num;
		String val;

		public InnerClass(int num, String val) {
			this.num = num;
			this.val = val;
		}

		public Integer getNum() {
			return num;
		}

		public void setNum(Integer num) {
			this.num = num;
		}

		public String getVal() {
			return val;
		}

		public void setVal(String val) {
			this.val = val;
		}

		@Override
		public String toString() {
			return "InnerClass [num=" + num + ", val=" + val + "]";
		}

	}

	public static void main(String[] args) {
		List<InnerClass> list = new ArrayList<InnerClass>();
		list.add(new InnerClass(1, "1232"));
		list.add(new InnerClass(2, "3343"));
		list.add(new InnerClass(1, "2233"));
		list.add(new InnerClass(2, "34324"));
		Map<Integer, List<InnerClass>> result = groupColl(list, new GroupBy<InnerClass, Integer>() {
			public Integer groupby(InnerClass inner) {
				return inner.getNum();
			}
		});
		Iterator<Integer> keyIter = result.keySet().iterator();
		while (keyIter.hasNext()) {
			Integer key = keyIter.next();
			for (InnerClass tmp : result.get(key)) {
			}
		}
		Map<Integer, InnerClass> result2 = coll2Map(list, new PrimaryKey<InnerClass, Integer>() {
			public Integer keyby(InnerClass d) {
				return d.getNum();
			}
		});
		for (Integer tmp : result.keySet()) {
		}
	}
}