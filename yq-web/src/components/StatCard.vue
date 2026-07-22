<script setup>
defineProps({
  label: String,
  value: [String, Number],
  hint: String,
  icon: String,
  tone: { type: String, default: 'green' },
})
</script>

<template>
  <div class="stat-card">
    <div class="stat-card__icon" :class="`is-${tone}`">
      <component :is="icon" />
    </div>
    <div class="stat-card__content">
      <span>{{ label }}</span>
      <strong>{{ value }}</strong>
      <small>{{ hint }}</small>
    </div>
  </div>
</template>
