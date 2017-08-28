# KwWarp
> Código fonte livre do plugin KwWarp, o plugin há API para desenvolvedores a partir da versão 0.3

Faça o download [clicando aqui](http://devkewi.esy.es/plugins/kwwarp/)

<br><br>

### API consiste em dois eventos:
```
 PlayerTeleportWarp
 PlayerLeaveWarp
```

<br><br>

### PlayerTeleportWarp
```
e.getWarpName(); //Nome da warp
e.getPlayers(); //Para todas as warps
e.getPlayersWarp(warp); //Para todos os jogadores dentro de uma warp
e.getPlayer(); //Pegar o jogador;
e.getPos1(); //Pegar o primeiro lugar da area setada
e.getPos2(); //Pegar o segundo lugar da area setada
e.getVezes(); //Pegar quantidade de vezes que o jogador teleportou-se para uma warp
e.getWarp(); //Local onde foi setado (onde os jogadores se teleportaram)
```

<br><br>

### PlayerLeaveWarp
```
e.getPlayer();
e.getPlayersWarp("");
e.getPos1();
e.getPos2();
e.withinArea(); // Verificar se está dentro da area
e.getWarpName();
```

<br><br>

### Exemplos de como usar a API:
```
@EventHandler
	void onTeleportWarp(PlayerTeleportWarp e) {
		String warp = e.getWarpName(); // Nome da warp
		if (warp.equalsIgnoreCase("pvp")) { //Verificando se o nome da warp é igual a pvp
			e.getPlayer().sendMessage("Esta warp está com PVP ON! Cuidado!");
			e.setCanceled(true);
		}
	}

	@EventHandler
	void onQuitWarp(PlayerLeaveWarp e) {
		String warp = e.getWarpName(); // Nome da warp
		if (warp.equalsIgnoreCase("pvp")) {
			e.getPlayer().sendMessage("Você saiu da warp pvp!");
			e.setCanceled(true);
		}
	}
```

[Exemplo em vídeo](https://youtu.be/M1Bu48GjCh8)
