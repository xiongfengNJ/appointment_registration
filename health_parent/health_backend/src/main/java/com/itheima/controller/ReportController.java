package com.itheima.controller;

/**
 * @author ：seanyang
 * @date ：Created in 2019/7/25
 * @description ：
 * @version: 1.0
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConst;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.OrderService;
import com.itheima.service.ReportService;
import com.itheima.service.SetmealService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计报表
 */
@RestController
@RequestMapping("/report")
public class ReportController {

	@Reference
	private MemberService memberService;

	@Reference
	private OrderService orderService;

	@Reference
	private ReportService reportService;

	/**
	 * 获取前12个月，每月会员累计增长总数量
	 * @return
	 */
	@RequestMapping("/getMemberReport")
	public Result getMemberReport(){

		Calendar calendar = Calendar.getInstance();
		// 返回到12个月之前的日期
		calendar.add(Calendar.MONTH,-12);
		try{
			// 获取月份列表
			List<String> monthList = new ArrayList();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			for (int i= 0 ;i!=12;i++){
				calendar.add(Calendar.MONTH,1);
				monthList.add(sdf.format(calendar.getTime()));
			}
			// 获取月份列表中月份的成员人数
			List<Integer> memberCount = memberService.findMemberCountByMonth(monthList);

			// 封装到返回数据
			Map<String,Object> maps	 = new HashMap<>();
			maps.put("months",monthList);
			maps.put("memberCount",memberCount);
			return new Result(true, MessageConst.ACTION_SUCCESS,maps);
		}catch(Exception e){
			e.printStackTrace();
		}
		return new Result(false,MessageConst.ACTION_FAIL);
	}

	@RequestMapping("/getSetmealReport")
	public Result getSetmealReport(){
		try{
			List<Map<String,Object>> mapList = orderService.findSetmealCount();
			List<String> setmealNameList = new ArrayList<>();
			// 把套餐名称取出来，放入列表
			for (Map oneMap:mapList){
				setmealNameList.add((String)oneMap.get("name"));
			}
			// 组合套餐占比数据
			Map resultMap = new HashMap();
			resultMap.put("setmealCount",mapList);
			resultMap.put("setmealNames",setmealNameList);
			return  new Result(true,MessageConst.QUERY_SETMEAL_SUCCESS,resultMap);
		}catch(Exception e){
		    e.printStackTrace();
		    return new Result(false,MessageConst.QUERY_SETMEAL_FAIL);
		}
	}

	@RequestMapping("/getBusinessReportData")
	public Result getBusinessReportData(){
		try{
			Map<String,Object> map = reportService.getBusinessReportData();
			return new Result(true,MessageConst.ACTION_SUCCESS,map);
		}catch(Exception e){
		    e.printStackTrace();
		    return new Result(false,MessageConst.ACTION_FAIL);
		}
	}

	/**
	 * 1. 调用Service获取报表数据
	 * 2. 利用POI把数据写入Excel模板文件
	 * 3. 成功，以流形式输出excel到客户端
	 * 4. 失败，返回错误信息
	 * @return
	 */
	@RequestMapping("/exportBusinessReport")
	public Result exportBusinessReport(HttpServletResponse response){
		/**
		 * reportDate:null,
		 *todayNewMember :0,
		 *totalMember :0,
		 *thisWeekNewMember :0,
		 *thisMonthNewMember :0,
		 *todayOrderNumber :0,
		 *todayVisitsNumber :0,
		 *thisWeekOrderNumber :0,
		 *thisWeekVisitsNumber :0,
		 *thisMonthOrderNumber :0,
		 *thisMonthVisitsNumber :0,
		 *hotSetmeal:[]
		 */
		try{
			Map<String,Object> map = reportService.getBusinessReportData();
			// 报表日期
			String orderDate = (String)map.get("reportDate");
			// 会员相关
			Integer todayNewMember = (Integer) map.get("todayNewMember");
			Integer totalMember = (Integer) map.get("totalMember");
			Integer thisWeekNewMember = (Integer) map.get("thisWeekNewMember");
			Integer thisMonthNewMember = (Integer) map.get("thisMonthNewMember");

			// 读Excel模板文件
			InputStream excelFileInputStream = this.getClass().getResourceAsStream("/report_template.xlsx");
			// 构建workbook对象，并获取sheet
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(excelFileInputStream);
			XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
			// 获取报表日期行
			XSSFRow row = sheet.getRow(2);
			row.getCell(5).setCellValue(orderDate);
			// 获取新增会员及总会员行
			row = sheet.getRow(4);
			row.getCell(5).setCellValue(todayNewMember);
			row.getCell(7).setCellValue(totalMember);
			// 获取本周及本月新增会员数
			row = sheet.getRow(5);
			row.getCell(5).setCellValue(thisWeekNewMember);
			row.getCell(7).setCellValue(thisMonthNewMember);

			/**
			 *todayOrderNumber :0,
			 *todayVisitsNumber :0,
			 *thisWeekOrderNumber :0,
			 *thisWeekVisitsNumber :0,
			 *thisMonthOrderNumber :0,
			 *thisMonthVisitsNumber :0,
			 */
			// 获取今日预约及今日到诊
			row = sheet.getRow(7);
			row.getCell(5).setCellValue((Integer)map.get("todayOrderNumber"));
			row.getCell(7).setCellValue((Integer)map.get("todayVisitsNumber"));
			// 获取本周预约及到诊
			row = sheet.getRow(8);
			row.getCell(5).setCellValue((Integer)map.get("thisWeekOrderNumber"));
			row.getCell(7).setCellValue((Integer)map.get("thisWeekVisitsNumber"));
			// 获取本月预约及到诊
			row = sheet.getRow(9);
			row.getCell(5).setCellValue((Integer)map.get("thisMonthOrderNumber"));
			row.getCell(7).setCellValue((Integer)map.get("thisMonthVisitsNumber"));

			int startNumber = 12;
			List<Map> hotSetmealList = (List<Map>) map.get("hotSetmeal");
			for (Map oneMap:hotSetmealList){
				String name = (String)oneMap.get("name");
				Long setmeal_count = (Long)oneMap.get("setmeal_count");
				BigDecimal bigDecimal = (BigDecimal) oneMap.get("proportion");
				row = sheet.getRow(startNumber);
				row.getCell(4).setCellValue(name);
				row.getCell(5).setCellValue(setmeal_count);
				row.getCell(6).setCellValue(bigDecimal.doubleValue());
				startNumber++;
			}

			// 下载
			ServletOutputStream outputStream = response.getOutputStream();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("content-Disposition","attachment;filename="+orderDate+"_report.xlsx");
			xssfWorkbook.write(outputStream);
			outputStream.flush();
			outputStream.close();
			xssfWorkbook.close();
			return  null;
		}catch(Exception e){
		    e.printStackTrace();
		    return new Result(false,MessageConst.ACTION_FAIL);
		}
	}
}
