package com.cera.chain.appframe;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.springframework.data.redis.core.RedisTemplate;

import com.atomikos.icatch.jta.UserTransactionImp;

public class FrameUserTransaction extends UserTransactionImp {
	private static final long serialVersionUID = 1L;
	private RedisTemplate<?,?> template;

	public void begin() throws NotSupportedException, SystemException {
		super.begin();
		template.setEnableTransactionSupport(true);
	}

	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException,
			IllegalStateException, SecurityException {
		super.commit();
		template.setEnableTransactionSupport(false);
	}

	public void rollback() throws IllegalStateException, SystemException, SecurityException {
		super.rollback();
		template.setEnableTransactionSupport(false);
	}
	
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		super.setRollbackOnly();
		template.setEnableTransactionSupport(false);
	}

	public RedisTemplate<?,?> getTemplate() {
		return template;
	}

	public void setTemplate(RedisTemplate<?,?> template) {
		this.template = template;
	}

}
