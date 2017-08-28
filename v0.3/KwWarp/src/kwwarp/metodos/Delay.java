package kwwarp.metodos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Delay {

	private Metodos m;
	private Map<String, Long> delay = new HashMap<String, Long>();
	private Map<String, Long> tempo = new HashMap<String, Long>();

	public Delay(Metodos m) {
		this.m = m;
	}

	public void adicionarDelay(String jogador, int segundos) {
		delay.put(jogador, System.currentTimeMillis());
		tempo.put(jogador, TimeUnit.SECONDS.toMillis(segundos));
	}

	public boolean temDelay(String jogador) {
		if (delay.containsKey(jogador))
			return true;
		return false;
	}

	public void removerDelay(String jogador) {
		if (delay.containsKey(jogador))
			delay.remove(jogador);
		if (tempo.containsKey(jogador))
			tempo.remove(jogador);
	}

	public boolean acabouDelay(String jogador) {
		String tempo = getTempo(jogador);
		if (tempo.equals("um momento"))
			return true;
		return false;
	}

	public long getSegundos(String jogador) {
		if (tempo.containsKey(jogador))
			return tempo.get(jogador);
		return 1;
	}

	public long getDelay(String jogador) {
		if (delay.containsKey(jogador))
			return delay.get(jogador);
		return System.currentTimeMillis();
	}

	public String getTempo(String jogador) {
		long tempo_antes = getDelay(jogador);
		long tempo_atual = (System.currentTimeMillis() - getSegundos(jogador));
		String tempo = getFormato((tempo_antes - tempo_atual));
		if (tempo.endsWith("e ")) {
			tempo = tempo.substring(0, tempo.length() - 2);
		}
		return tempo;
	}

	private String getFormato(long tempo) {
		if (tempo == 0)
			return "nunca";
		long dia = TimeUnit.MILLISECONDS.toDays(tempo);
		long horas = TimeUnit.MILLISECONDS.toHours(tempo) - (dia * 24);
		long minutos = TimeUnit.MILLISECONDS.toMinutes(tempo) - (TimeUnit.MILLISECONDS.toHours(tempo) * 60);
		long segundos = TimeUnit.MILLISECONDS.toSeconds(tempo) - (TimeUnit.MILLISECONDS.toMinutes(tempo) * 60);
		StringBuilder sb = new StringBuilder();
		if (dia > 0)
			sb.append(getNumero(dia) + " " + (dia == 1 ? "dia e" : "dias e") + " ");
		if (horas > 0)
			sb.append(getNumero(horas) + " " + (horas == 1 ? "hora e" : "horas e") + " ");
		if (minutos > 0)
			sb.append(getNumero(minutos) + " " + (minutos == 1 ? "minuto e" : "minutos e") + " ");
		if (segundos > 0)
			sb.append(getNumero(segundos) + " " + (segundos == 1 ? "segundo e" : "segundos e") + " ");
		return sb.toString().isEmpty() ? "um momento" : sb.toString();
	}

	private String getNumero(long tempo) {
		if (m.getTexto("Warp.Delay.Numeros") != null)
			if (!m.getConfig().getBoolean("Warp.Delay.Numeros")) {
				if (tempo == 1)
					return "um";
				if (tempo == 2)
					return "dois";
				if (tempo == 3)
					return "três";
				if (tempo == 4)
					return "quatro";
				if (tempo == 5)
					return "cinco";
				if (tempo == 6)
					return "seis";
				if (tempo == 7)
					return "sete";
				if (tempo == 8)
					return "oito";
				if (tempo == 9)
					return "nove";
				if (tempo == 10)
					return "dez";
				if (tempo == 11)
					return "onze";
				if (tempo == 12)
					return "doze";
				if (tempo == 13)
					return "treze";
				if (tempo == 14)
					return "catoze";
				if (tempo == 15)
					return "quinze";
				if (tempo == 16)
					return "dizasseis";
				if (tempo == 17)
					return "dizassete";
				if (tempo == 18)
					return "dezoito";
				if (tempo == 19)
					return "dezanove";
				if (tempo == 20)
					return "vinte";
				if (tempo == 21)
					return "vinte e um";
				if (tempo == 22)
					return "vinte e dois";
				if (tempo == 23)
					return "vinte e três";
				if (tempo == 24)
					return "vinte e quatro";
				if (tempo == 25)
					return "vinte e cinco";
				if (tempo == 26)
					return "vinte e seis";
				if (tempo == 27)
					return "vinte e sete";
				if (tempo == 28)
					return "vinte e oito";
				if (tempo == 29)
					return "vinte e nove";
				if (tempo == 30)
					return "trinta";
				if (tempo == 31)
					return "trinta e um";
				if (tempo == 32)
					return "trinta e dois";
				if (tempo == 33)
					return "trinta e três";
				if (tempo == 34)
					return "trinta e quatro";
				if (tempo == 35)
					return "trinta e cinco";
				if (tempo == 36)
					return "trinta e seis";
				if (tempo == 37)
					return "trinta e sete";
				if (tempo == 38)
					return "trinta e oito";
				if (tempo == 39)
					return "trinta e nove";
				if (tempo == 40)
					return "quarenta";
				if (tempo == 41)
					return "quarenta e um";
				if (tempo == 42)
					return "quarenta e dois";
				if (tempo == 43)
					return "quarenta e três";
				if (tempo == 44)
					return "quarenta e quatro";
				if (tempo == 45)
					return "quarenta e cinco";
				if (tempo == 46)
					return "quarenta e seis";
				if (tempo == 47)
					return "quarenta e sete";
				if (tempo == 48)
					return "quarenta e oito";
				if (tempo == 49)
					return "quarenta e nove";
				if (tempo == 50)
					return "cinquenta";
				if (tempo == 51)
					return "cinquenta e um";
				if (tempo == 52)
					return "cinquenta e dois";
				if (tempo == 53)
					return "cinquenta e três";
				if (tempo == 54)
					return "cinquenta e quatro";
				if (tempo == 55)
					return "cinquenta e cinco";
				if (tempo == 56)
					return "cinquenta e seis";
				if (tempo == 57)
					return "cinquenta e sete";
				if (tempo == 58)
					return "cinquenta e oito";
				if (tempo == 59)
					return "cinquenta e nove";
			}
		return Long.toString(tempo);
	}

	/**
	 * @author Kewilleen G
	 * @version 0.1
	 * 
	 */

	//
	// Como usar:
	// if (!temDelay("jogador")) {
	// adicionarDelay("jogador", 10);
	// System.out.println("Delay adicionado");
	// return true;
	// }
	// if (acabouDelay("jogador")) {
	// removerDelay("jogador");
	// System.out.println("Acabou delay");
	// return true;
	// }
	// System.out.println(getTempo("jogador"));
	// return true;
	//
}
