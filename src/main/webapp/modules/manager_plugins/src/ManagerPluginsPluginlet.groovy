import com.alibaba.fastjson.JSONimport org.marker.mushroom.beans.GuestBookimport org.marker.mushroom.beans.ResultMessageimport org.marker.mushroom.core.SystemStaticimport org.marker.mushroom.core.config.impl.DataBaseConfigimport org.marker.mushroom.dao.ISupportDaoimport org.marker.mushroom.ext.plugin.ViewObjectimport org.marker.mushroom.utils.ResultUtilsimport org.springframework.web.bind.annotation.ResponseBodyimport javax.servlet.http.HttpServletRequestimport org.marker.mushroom.holder.SpringContextHolderimport java.lang.Integerimport javax.servlet.http.HttpServletRequestimport javax.servlet.http.HttpServletResponseimport org.marker.mushroom.ext.plugin.Pluginletimport org.marker.mushroom.alias.DAO/** * 插件管理器 插件 * * * @author marker * */public class ManagerPluginsPluginletImpl extends Pluginlet {	ManagerPluginsPluginletImpl(){		this._config = [                module: "plugins",    			name  : "留言插件",    			author: "marker",     			type : "plugins",// 模型标识    			description : "常规插件"  		   ]				// 路由配置		this.routers = [ 		      " get:/list": "list",       		      "post:/add" : "submit",              "get:/delete" : "delete",			  " get:/audit" : "audit",        ]	}				/**	 * 插件列表	 */	String list(){		HttpServletRequest request = getServletRequest();		int currentPageNo = 1;		try{			currentPageNo = Integer.parseInt(request.getParameter("currentPageNo"));		}catch(Exception e){ }		ISupportDao dao = SpringContextHolder.getBean(DAO.COMMON);        DataBaseConfig dbcfg = DataBaseConfig.getInstance();        String sql = "select * from "+ dbcfg.getPrefix()  +"guestbook order by id desc";		request.setAttribute("page",  dao.findByPage(currentPageNo, 15, sql));		return "list.html";	}    /**     * 显示留言列表     */    @ResponseBody	def delete() {        HttpServletRequest request = getServletRequest();        String rid = request.getParameter("rid");        ISupportDao dao = SpringContextHolder.getBean(DAO.COMMON);        dao.deleteByIds(GuestBook.class, rid);        return ResultUtils.success("删除成功！")    }			/**	 * 提交	 */    @ResponseBody	def submit(){		HttpServletRequest request = getServletRequest();		String code = request.getParameter("authcode");// 验证码 		String randauthcode = (String) request.getSession().getAttribute("randauthcode");		String ip  = request.getRemoteHost();		String nicknamea = request.getParameter("nickname");		String content  = request.getParameter("content");        DataBaseConfig dbcfg = DataBaseConfig.getInstance();				String msg = "thanks for you";		if(randauthcode != null && randauthcode.toLowerCase().equals(code.toLowerCase())){            String sql = "insert into " + dbcfg.getPrefix()+"guestbook(nickname,ip,content,time,status) values(?,?,?,sysdate(),0)";            def strs = [nicknamea,ip,content] as Object[];			if(!commonDao.update(sql, strs)){				msg = "亲，您留言数据无效，已经进行数据回滚";			}		}else{			msg = "亲，验证码错了哈";		}        return ResultUtils.success(msg)	}	/**	 * 留言审核	 */	@ResponseBody	def audit() {        HttpServletRequest request = getServletRequest();        String idStr = request.getParameter("id");// 验证码        int id = Integer.parseInt(idStr);        DataBaseConfig dbcfg = DataBaseConfig.getInstance();        String sql = "update " + dbcfg.getPrefix() + "guestbook set status=1 where id=?";        Object[] params =  [id] as Object[]        if (!commonDao.update(sql, params)) {            return ResultUtils.error("审核失败！")        }		return ResultUtils.success("审核成功！")    }    /**     * 前端留言组件     */    def view(){        HttpServletRequest request = getServletRequest();        return "guestbook.html";    }}