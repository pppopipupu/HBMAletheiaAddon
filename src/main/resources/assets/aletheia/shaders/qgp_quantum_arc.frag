uniform float time;

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    float dist = abs(uv.y - 0.5) * 2.0;

    float core = clamp(1.0 - dist * 3.0, 0.0, 1.0);
    float glow = clamp(1.0 - dist, 0.0, 1.0);

    float r = 0.5 + 0.5 * sin(time * 12.0 + uv.x * 25.0);
    float g = 0.5 + 0.5 * sin(time * 12.0 + uv.x * 25.0 + 2.094);
    float b = 0.5 + 0.5 * sin(time * 12.0 + uv.x * 25.0 + 4.188);

    vec3 rgbGlow = vec3(r, g, b) * glow * 1.8;
    vec3 whiteCore = vec3(1.0, 1.0, 1.0) * core * 2.5;

    vec3 finalColor = rgbGlow + whiteCore;
    float alpha = clamp(glow * 1.2, 0.0, 1.0);

    gl_FragColor = vec4(finalColor, alpha);
}
