package me.joelsoep.ctf.game.special.specials;

public class TNTArrow /**extends BaseSpecial<EntityShootBowEvent>**/ {
    /**public TNTArrow() {
        super("ExplosiveArrow");
    }

    @Override
    protected ItemStack getItemStack() {
        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta potMeta = (PotionMeta) arrow;
        potMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        arrow.setItemMeta(potMeta);
        return arrow;
    }

    @Override
    @EventHandler
    public void onUse(EntityShootBowEvent event) {
        Entity livingEntity = event.getEntity();
        Entity projectile = event.getProjectile();
        ItemStack itemStack = event.getConsumable();

        if (!(livingEntity instanceof Player player)) {
            return;
        }

        if (itemStack == null) {
            return;
        }

        if (getItemStack().getType() != itemStack.getType() || !itemStack.hasItemFlag(ItemFlag.HIDE_DYE)) {
            return;
        }
        if (!(projectile instanceof Arrow arrow)) {
            return;
        }

        if (arrow.getItemStack().geu)
        arrow.addPassenger(player);
        int x = arrow.getLocation().getBlockX();
        while (!arrow.isOnGround() && !arrow.isDead()) {
            if (x == arrow.getLocation().getBlockX()) {
                return;
            }

            x = arrow.getLocation().getBlockX();
            arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.PRIMED_TNT);
        }

        arrow.customName(MM.deserialize("explosive_arrow"));
    }
    **/
}
