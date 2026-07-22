<script setup>
defineProps({
  eyebrow: { type: String, default: '教学运营' },
  title: { type: String, required: true },
  description: { type: String, default: '' },
})
</script>

<template>
  <div class="page-heading">
    <div>
      <div class="page-heading__eyebrow">{{ eyebrow }}</div>
      <h1>{{ title }}</h1>
      <p v-if="description">{{ description }}</p>
    </div>
    <div class="page-heading__actions">
      <slot />
    </div>
  </div>
</template>
