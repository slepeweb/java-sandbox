package com.slepeweb.sandbox.spizza.bean;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.slepeweb.sandbox.spizza.bean.PizzaFactory.Base;
import com.slepeweb.sandbox.spizza.bean.PizzaFactory.Size;
import com.slepeweb.sandbox.spizza.bean.PizzaFactory.Topping;

public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	private static int ORDER_NUMBER_RECORD = 1001;
	private int id;
	private Customer customer;
	private List<Pizza> pizzas;
	private Payment payment;
	private String deliveryEta;

	public static synchronized Order getInstance() {
		Order o = new Order();
		o.setId(ORDER_NUMBER_RECORD++);
		return o;
	}

	private Order() {
		pizzas = new ArrayList<Pizza>();
		customer = new Customer();
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Pizza> getPizzas() {
		return pizzas;
	}

	public void setPizzas(List<Pizza> pizzas) {
		this.pizzas = pizzas;
	}

	public void addPizza(PizzaForm pizzaForm) {
		Base b = Base.valueOf(pizzaForm.getBase());
		Size s = Size.valueOf(pizzaForm.getSize());
		List<Topping> toppings = new ArrayList<Topping>();
		
		/*
		 * The toppings are input via checkboxes. If not toppings are selected,
		 * then pizzaForm.getToppings() is null, EVEN THOUGH it is initialised 
		 * in the PizzaForm class.
		 */
		if (pizzaForm.getToppings() != null) {
			for (String t : pizzaForm.getToppings()) {
				toppings.add(Topping.valueOf(t));
			}
		}
		
		Pizza p = PizzaFactory.getPizza(b, s);
		p.setToppings(toppings);
		getPizzas().add(p);
	}

	public float getTotal() {
		float total = 0.0f;
		for (Pizza p : getPizzas()) {
			total += p.getPrice();
		}
		
		return total;
	}
	
	public String getTotalFormatted() {
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);
		return nf.format(getTotal());
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeliveryEta() {
		return deliveryEta;
	}

	public void setDeliveryEta(String deliveryEta) {
		this.deliveryEta = deliveryEta;
	}
}