uniform float time;

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    vec2 center = vec2(0.5, 0.5);
    vec2 dir = uv - center;
    float dist = length(dir);

    float angle = atan(dir.y, dir.x);
    float ringGlow = smoothstep(0.5, 0.3, abs(dist - 0.35));

    float spiral = sin(angle * 8.0 - time * 10.0 + dist * 20.0);
    float pulse = 0.5 + 0.5 * spiral;

    float r = 0.5 + 0.5 * sin(time * 8.0 + angle * 3.0);
    float g = 0.5 + 0.5 * sin(time * 8.0 + angle * 3.0 + 2.094);
    float b = 0.5 + 0.5 * sin(time * 8.0 + angle * 3.0 + 4.188);

    vec3 color = vec3(r, g, b) * (0.6 + 0.8 * pulse) * 2.0;
    vec3 coreWhite = vec3(1.0, 1.0, 1.0) * pow(ringGlow, 3.0) * 1.5;

    gl_FragColor = vec4(color + coreWhite, ringGlow * 0.95);
}
