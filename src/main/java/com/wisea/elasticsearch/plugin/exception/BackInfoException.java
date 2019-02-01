package com.wisea.elasticsearch.plugin.exception;

import org.elasticsearch.ElasticsearchException;

/**
 * 备份信息异常
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月30日 下午1:36:16
 */
public class BackInfoException extends ElasticsearchException {
    public BackInfoException(String msg, Throwable cause, Object[] args) {
        super(msg, cause, args);
        // TODO Auto-generated constructor stub
    }

    public BackInfoException(Throwable cause) {
        super(cause);
    }
    
    public BackInfoException(String cause) {
        super(cause);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -8300450336535719513L;

}
