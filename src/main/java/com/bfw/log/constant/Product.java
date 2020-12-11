package com.bfw.log.constant;

/**
 * @author Lixiang
 * @description 产品枚举表
 */
public enum Product {
	/**
	 * 斗球
	 */
	DQ(1,"斗球",0),
	
	/**
	 * 玩球帝
	 */
	WQD(100,"玩球帝",1),
	
	/**
	 * 蜜柚
	 */
	MY(2,"蜜柚",0),
	
	/**
	 * 蜜豆
	 */
	MD(101,"蜜豆",2),
	
	/**
	 * 蜜柚2
	 */
	MY2(102,"蜜柚2",2),
	
	/**
	 * 功能监控
	 */
	GNK(3,"功能监控",0),
	
	/**
	 * 性能监控
	 */
	XNJK(4,"性能监控",0)
	;
	
	/**
	 * 产品ID
	 */
	private Integer ProductId;
	
	/**
	 * 产品名称
	 */
	private String ProductName;
	
	/**
	 * 父级产品ID
	 */
	private Integer ParentId;
	
	/**
	 * 产品备注说明
	 */
	private String Comment;
	
	public static final Product getProduct(Integer ProductId){
		for(Product product:Product.values()) if(product.ProductId.equals(ProductId)) return product;
		return null;
	}
	
	public static final Product getProduct(String ProductIdStr){
		for(Product product:Product.values()) if(product.name().equals(ProductIdStr)) return product;
		return null;
	}
	
	private Product(Integer ProductId,String ProductName,Integer ParentId){
		this.ParentId=ParentId;
		this.ProductId=ProductId;
		this.Comment=ProductName;
		this.ProductName=ProductName;
	}
	
	public Integer getProductId() {
		return ProductId;
	}

	public String getProductName() {
		return ProductName;
	}

	public Integer getParentId() {
		return ParentId;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String Comment){
		this.Comment=Comment;
	}
}
