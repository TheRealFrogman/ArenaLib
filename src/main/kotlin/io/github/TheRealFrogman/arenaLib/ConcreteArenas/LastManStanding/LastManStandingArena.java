package io.github.TheRealFrogman.arenaLib.ConcreteArenas.LastManStanding;

public final class LastManStandingArena /**extends Arena implements
        ISoloArenaMarker, IRoundedArenaMarker, WinnableArena, Scored, KillPlayerArena, ISessionedArena*/ {
//    private int minPlayers;
//    private int maxPlayers;
//    private final List<SpawnPoint> spawnPoints;
//
//    LastManStandingArena(int minPlayers, int maxPlayers, Set<ArenaPlayer> players, List<SpawnPoint> spawnPoints) {
//        this.minPlayers = minPlayers;
//        this.maxPlayers = maxPlayers;
//        this.players.addAll(players);
//        this.spawnPoints = spawnPoints;
//
//        if(maxPlayers > spawnPoints.size())
//            throw new IllegalArgumentException("Max players can't be more than spawn points");
//    }
//
//    public void declareWinners(@Nonnull Set<ArenaPlayer> winners) {
//
//        Location spawnLocation = Bukkit.getServer().getWorld("world").getSpawnLocation();
//        getWinner().getWrappedBukkitPlayer().teleport(spawnLocation);
//    }
//
//    @Override
//    protected boolean isPlayersEnough() {
//        return players.size() == minPlayers;
//    }
//    @Nullable
//    public ArenaPlayer getWinner() {
//        if(getWinners() != null) {
//            return getWinners().stream().findFirst().orElse(null);
//        } else return null;
//    }
//    @Override
//    protected void onArenaStart(@Nonnull ArenaBase arena) {
//        List<ArenaPlayer> listedPlayers = new ArrayList<>(this.players);
//        Collections.shuffle(listedPlayers); // чтобы нельзя было предсказать спавн
//
//        for (int i = 0; i < listedPlayers.size(); i++) {
//            ArenaPlayer currentPLayer = listedPlayers.get(i);
//            SpawnPoint currentSpawnpoint = this.spawnPoints.get(i);
//
//            currentSpawnpoint.spawnPlayer(currentPLayer.getWrappedBukkitPlayer());
//        }
//    }
//    @Override
//    protected void onArenaFinish(@Nonnull ArenaBase arena) {}
//
//    private final void onKill(ArenaPlayer killer, ArenaPlayer victim){
//        if(isPlayerIn_PlayersList(killer) && isPlayerIn_ArenaRegion(killer)
//                && isPlayerIn_PlayersList(victim) && isPlayerIn_ArenaRegion(victim)
//        ) {
//            this.players.remove(victim); // может и не надо убирать их
//            this.scoreboard.addScore(killer, 1);
//
//            if(checkWinCondition()) {
//                Set<ArenaPlayer> s = new HashSet<>();
//                s.add(killer);
//                declareWinners(s);
//            } else {
//                StringBuilder stringBuilder = new StringBuilder();
//                players.forEach(arenaPlayer -> {
//                    stringBuilder
//                            .append("Осталось")
//                            .append(" ")
//                            .append(players.size())
//                            .append(" ")
//                            .append("игроков");
//
//                    Player bukkitPlayer = arenaPlayer.getWrappedBukkitPlayer();
//                    bukkitPlayer.sendMessage(stringBuilder.toString());
//                });
//            }
//        }
//    }
}
