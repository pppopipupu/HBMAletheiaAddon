uniform sampler2D texture;
uniform float time;

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    vec2 center = vec2(0.5, 0.5);
    float dist = length(uv - center);

    vec4 color = texture2D(texture, uv);

    if (color.a > 0.01) {
        float pulse = 0.5 + 0.5 * sin(time * 8.0 - dist * 15.0);
        vec3 holeColor = mix(vec3(0.05, 0.0, 0.1), vec3(0.9, 0.1, 0.3), pulse);

        if (dist < 0.25) {
            color.rgb = vec3(0.02, 0.0, 0.04);
        } else {
            color.rgb = mix(color.rgb, holeColor, 0.65) + holeColor * 0.4;
        }
    }

    gl_FragColor = color;
}
